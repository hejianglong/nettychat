/*
 * Project: com.im.nettychat
 * 
 * File Created at 2018/12/22
 * 
 * Copyright 2018 CMCC Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * ZYHY Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.im.nettychat;

import com.im.nettychat.common.Command;
import com.im.nettychat.protocol.PacketCodec;
import com.im.nettychat.protocol.request.LoginRequest;
import com.im.nettychat.protocol.request.MessageRequest;
import com.im.nettychat.protocol.response.LoginResponse;
import com.im.nettychat.protocol.response.MessageResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;
import java.util.Scanner;

/**
 * @author hejianglong
 * @Desc
 * @date 2018/12/22 下午7:04
 */
public class MainTest {
    // 客户端互相聊天
    public static void main(String[] args) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        bootstrap
            .group(workerGroup)
            .channel(NioSocketChannel.class)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .option(ChannelOption.TCP_NODELAY, true)
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast(new PacketDecoder());
                    // 收到服务器返回来的消息
                    ch.pipeline().addLast(new LoginResponseHandler());
                    ch.pipeline().addLast(new MessageResponseHandler());
                }
            });
        ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8080).addListener(future -> {
            if (future.isSuccess()) {
                // 建立连接成功后发起登录请求或者注册请求
                Channel channel = ((ChannelFuture) future).channel();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (!Thread.interrupted()) {
                            Scanner scanner = new Scanner(System.in);
                            System.out.println("---输入指令 -> a: 登录, b: 发送消息");
                            String command = scanner.nextLine();
                            if (command.equals("a")) {
                                System.out.println("开始登录");
                                System.out.println("请输入用户名和密码用,隔开");
                                String userInfo = scanner.nextLine();
                                String[] userInfoArr = userInfo.split(",");
                                // 发起注册
                                login(channel, userInfoArr[0], userInfoArr[1]);
                            } else if (command.equals("b")) {
                                System.out.println("请输入对方的id和发送的消息用,隔开: ");
                                String userAndMessage = scanner.nextLine();
                                String[] us = userAndMessage.split(",");
                                sendMessage(channel, Long.valueOf(us[0]), us[1]);
                            }
                        }
                    }
                }).start();
            }
        });
        channelFuture.sync().channel().closeFuture().sync();
    }

    private static void login(Channel channel, String username, String password) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);
        ByteBuf byteBuf = channel.alloc().buffer();
        PacketCodec.INSTANCE.encode(byteBuf, loginRequest);
        channel.writeAndFlush(byteBuf);
        waitForResponse();
    }

    private static void sendMessage(Channel channel, Long userId, String message) {
        MessageRequest requestMessage = new MessageRequest();
        requestMessage.setToUserId(userId);
        requestMessage.setMessage(message);
        ByteBuf byteBuf = channel.alloc().buffer();
        PacketCodec.INSTANCE.encode(byteBuf, requestMessage);
        channel.writeAndFlush(byteBuf);
        waitForResponse();
    }

    private static void waitForResponse() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
    }
}
class PacketDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List out) {
        out.add(PacketCodec.INSTANCE.decode(in));
    }
}

class LoginResponseHandler extends SimpleChannelInboundHandler<LoginResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginResponse msg) throws Exception {
        System.out.println("---=-==--");
        if (msg.getCommand() == Command.LOGIN_RESPONSE) {
            // 如果是注册则返回注册响应指令
            LoginResponse response = (LoginResponse) msg;
            // 如果存在错误
            if (response.isError()) {
                System.out.println(response.getErrorInfo());
            } else {
                // 成功输出对象
                System.out.println(response);
            }
        }
    }
}

class MessageResponseHandler extends SimpleChannelInboundHandler<MessageResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageResponse msg) throws Exception {
        if (msg.getCommand() == Command.SEND_MESSAGE_RESPONSE) {
            // 如果是注册则返回注册响应指令
            MessageResponse registerResponse = (MessageResponse) msg;
            // 如果存在错误
            if (registerResponse.isError()) {
                System.out.println(registerResponse.getErrorInfo());
            } else {
                // 成功输出对象
                System.out.println("收到消息: [ " + registerResponse + " ]");
            }
        }
    }
}