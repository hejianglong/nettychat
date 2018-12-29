package com.chat.androidclient.greendao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.chat.androidclient.mvvm.model.MessageResponse;
import com.chat.androidclient.mvvm.model.Conversation;
import com.chat.androidclient.mvvm.model.Friend;
import com.chat.androidclient.mvvm.model.Group;

import com.chat.androidclient.greendao.MessageResponseDao;
import com.chat.androidclient.greendao.ConversationDao;
import com.chat.androidclient.greendao.FriendDao;
import com.chat.androidclient.greendao.GroupDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig messageResponseDaoConfig;
    private final DaoConfig conversationDaoConfig;
    private final DaoConfig friendDaoConfig;
    private final DaoConfig groupDaoConfig;

    private final MessageResponseDao messageResponseDao;
    private final ConversationDao conversationDao;
    private final FriendDao friendDao;
    private final GroupDao groupDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        messageResponseDaoConfig = daoConfigMap.get(MessageResponseDao.class).clone();
        messageResponseDaoConfig.initIdentityScope(type);

        conversationDaoConfig = daoConfigMap.get(ConversationDao.class).clone();
        conversationDaoConfig.initIdentityScope(type);

        friendDaoConfig = daoConfigMap.get(FriendDao.class).clone();
        friendDaoConfig.initIdentityScope(type);

        groupDaoConfig = daoConfigMap.get(GroupDao.class).clone();
        groupDaoConfig.initIdentityScope(type);

        messageResponseDao = new MessageResponseDao(messageResponseDaoConfig, this);
        conversationDao = new ConversationDao(conversationDaoConfig, this);
        friendDao = new FriendDao(friendDaoConfig, this);
        groupDao = new GroupDao(groupDaoConfig, this);

        registerDao(MessageResponse.class, messageResponseDao);
        registerDao(Conversation.class, conversationDao);
        registerDao(Friend.class, friendDao);
        registerDao(Group.class, groupDao);
    }
    
    public void clear() {
        messageResponseDaoConfig.clearIdentityScope();
        conversationDaoConfig.clearIdentityScope();
        friendDaoConfig.clearIdentityScope();
        groupDaoConfig.clearIdentityScope();
    }

    public MessageResponseDao getMessageResponseDao() {
        return messageResponseDao;
    }

    public ConversationDao getConversationDao() {
        return conversationDao;
    }

    public FriendDao getFriendDao() {
        return friendDao;
    }

    public GroupDao getGroupDao() {
        return groupDao;
    }

}
