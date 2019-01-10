package com.im.nettychat.service.impl;

import com.im.nettychat.cache.CacheName;
import com.im.nettychat.common.ErrorCode;
import com.im.nettychat.common.OfflineMessageType;
import com.im.nettychat.common.Session;
import com.im.nettychat.config.db.DBUtil;
import com.im.nettychat.domain.Friend;
import com.im.nettychat.domain.User;
import com.im.nettychat.domain.mapper.FriendMapper;
import com.im.nettychat.domain.mapper.UserMapper;
import com.im.nettychat.model.OfflineMessage;
import com.im.nettychat.protocol.dto.UserDTO;
import com.im.nettychat.protocol.request.LoginRequest;
import com.im.nettychat.protocol.request.MessageRequest;
import com.im.nettychat.protocol.request.RegisterRequest;
import com.im.nettychat.protocol.request.user.AddFriendRequest;
import com.im.nettychat.protocol.request.user.GetFriendRequest;
import com.im.nettychat.protocol.response.LoginResponse;
import com.im.nettychat.protocol.response.MessageResponse;
import com.im.nettychat.protocol.response.RegisterResponse;
import com.im.nettychat.protocol.response.offline.OfflineMessageResponse;
import com.im.nettychat.protocol.response.user.AddFriendResponse;
import com.im.nettychat.protocol.response.user.GetFriendResponse;
import com.im.nettychat.proxy.CglibServiceInterceptor;
import com.im.nettychat.service.BaseService;
import com.im.nettychat.service.UserService;
import com.im.nettychat.util.CollectionUtils;
import com.im.nettychat.util.SessionUtil;
import com.im.nettychat.util.Util;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.apache.ibatis.session.SqlSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import static com.im.nettychat.common.AttributeKeys.SESSION_ATTRIBUTE_KEY;
import static com.im.nettychat.model.RedisRepository.redisRepository;

/**
 * @author hejianglong
 * @date 2019/1/10.
 */
public class UserServiceImpl extends BaseService implements UserService {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(UserServiceImpl3.class);

    public static final UserService userService = (UserServiceImpl) CglibServiceInterceptor.getCglibProxy(UserServiceImpl.class);

    @Override
    public void login(ChannelHandlerContext ctx, LoginRequest request) {
        LoginResponse response = new LoginResponse();

        SqlSession sqlSession = DBUtil.getSession(true);
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            exceptionResponse(ctx, ErrorCode.USER_NOT_FOUND, response);
            return;
        }
        if (!user.isValidPassword(request.getPassword())) {
            exceptionResponse(ctx, ErrorCode.PASSWORD_ERROR, response);
            return;
        }
        sqlSession.close();
        response.setUserId(user.getId());
        response.setName(user.getName());
        response.setDesc(user.getDesc());
        response.setIcon(user.getIcon());

        bindSession(user, ctx.channel());
        ctx.writeAndFlush(response);
        // 登录成功后返回所有未读的离线消息
        sendOfflineMessageAndRemove(ctx, String.valueOf(user.getId()));
    }

    @Override
    public void register(ChannelHandlerContext ctx, RegisterRequest request) {
        RegisterResponse response = new RegisterResponse();

        SqlSession sqlSession = DBUtil.getSession(true);

        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        User user = userMapper.findByUsername(request.getUsername());
        if (user != null) {
            exceptionResponse(ctx, ErrorCode.USER_ALREADY_EXIST, response);
            return;
        }
        user = new User();
        user.setName(request.getName());
        user.setUsername(request.getUsername());
        user.setPassword(Util.hashPasswordAddingSalt(request.getPassword()));
        userMapper.save(user);
        redisRepository.vSetObject(CacheName.USER_INFO, String.valueOf(user.getId()), user);
        response.setUserId(user.getId());
        response.setName(user.getName());
        bindSession(user, ctx.channel());

        sqlSession.close();
        ctx.writeAndFlush(response);
    }

    @Override
    public void addFriend(ChannelHandlerContext ctx, AddFriendRequest request) {
        AddFriendResponse response = new AddFriendResponse();
        Long friendUserId = request.getFriendUserId();
        boolean exits = redisRepository.keyExits(CacheName.USER_INFO, String.valueOf(friendUserId));
        if(!exits) {
            exceptionResponse(ctx, ErrorCode.USER_NOT_FOUND, response);
            return;
        }
        Long userId = ctx.channel().attr(SESSION_ATTRIBUTE_KEY).get().getUserId();
        SqlSession sqlSession = DBUtil.getSession(true);
        FriendMapper friendMapper = sqlSession.getMapper(FriendMapper.class);
        Friend friend = new Friend();
        friend.setFromUserId(userId);
        friend.setToUserId(friendUserId);
        friendMapper.save(friend);
        redisRepository.sAdd(CacheName.USER_FRIEND, String.valueOf(userId), String.valueOf(friendUserId));
        ctx.writeAndFlush(response);
    }

    @Override
    public void getFriends(ChannelHandlerContext ctx, GetFriendRequest msg) {
        GetFriendResponse response = new GetFriendResponse();
        Long userId = ctx.channel().attr(SESSION_ATTRIBUTE_KEY).get().getUserId();
        Set<String> friendUserIds = redisRepository.sGet(CacheName.USER_FRIEND, String.valueOf(userId));
        List<UserDTO> friends = new ArrayList<>();
        if (CollectionUtils.isNotNullOrEmpty(friendUserIds)) {
            for (String friendId: friendUserIds) {
                User user = redisRepository.vGetObject(CacheName.USER_INFO, String.valueOf(friendId), User.class);
                if (user == null) {
                    logger.warn("friend userId is not exits: " + friendId);
                    continue;
                }
                UserDTO userDTO = new UserDTO();
                userDTO.setId(user.getId());
                userDTO.setUsername(user.getUsername());
                userDTO.setName(user.getName());
                userDTO.setIcon(user.getIcon());
                userDTO.setDesc(user.getDesc());
                friends.add(userDTO);
            }
        }
        response.setFriends(friends);
        ctx.writeAndFlush(response);
    }

    @Override
    public void sendMessage(ChannelHandlerContext ctx, MessageRequest request) {
        MessageResponse response = new MessageResponse();

        String message = request.getMessage();
        Long toUserId = request.getToUserId();
        boolean exits = redisRepository.keyExits(CacheName.USER_INFO, String.valueOf(toUserId));
        if(!exits) {
            exceptionResponse(ctx, ErrorCode.USER_NOT_FOUND, response);
            return;
        }
        Long fromUserId = ctx.channel().attr(SESSION_ATTRIBUTE_KEY).get().getUserId();
        Channel toChannel = SessionUtil.getChannel(toUserId);
        if (fromUserId.equals(toUserId)) {
            return;
        }
        // 发送离线消息
        if (toChannel == null) {
            // 存储到它的用户离线消息中
            OfflineMessage offlineMessage = new OfflineMessage();
            offlineMessage.setMessage(message);
            offlineMessage.setMessageType(OfflineMessageType.USER);
            offlineMessage.setUserId(toUserId);
            redisRepository
                    .lPush(CacheName.OFFLINE_MESSAGE, String.valueOf(toUserId), offlineMessage);
            return;
        }
        response.setMessage(message);
        response.setFromUserId(fromUserId);
        toChannel.writeAndFlush(response);
    }

    private void bindSession(User user, Channel channel) {
        Session session = new Session();
        session.setUserId(user.getId());
        session.setUsername(user.getUsername());
        SessionUtil.bindSession(session, channel);
    }

    private void sendOfflineMessageAndRemove(ChannelHandlerContext ctx, String userId) {
        OfflineMessageResponse offlineMessageResponse = new OfflineMessageResponse();
        List<OfflineMessage> offlineMessages = redisRepository.lRangeObject(CacheName.OFFLINE_MESSAGE, userId, 0, -1, OfflineMessage.class);
        offlineMessageResponse.setMessages(offlineMessages);
        ctx.writeAndFlush(offlineMessageResponse).addListener(future -> {
            // 消息推送成功后删除
            redisRepository.removeKey(CacheName.OFFLINE_MESSAGE, userId);
        });
    }
}
