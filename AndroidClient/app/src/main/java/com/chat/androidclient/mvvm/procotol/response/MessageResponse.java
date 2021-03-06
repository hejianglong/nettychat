/*
 * Project: com.im.nettychat.protocol.response
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
package com.chat.androidclient.mvvm.procotol.response;

import com.chat.androidclient.mvvm.model.Command;
import com.chat.androidclient.mvvm.model.Contact;
import com.chat.androidclient.mvvm.model.PacketResponse;
import com.chat.androidclient.mvvm.model.ConverSationTYPE;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author hejianglong
 * @Desc
 * @date 2018/12/22 下午6:16
 */
@Entity
public class MessageResponse extends PacketResponse {
    @Id
    private Long id;
    private Long fromUserId;
    private Long toUserId;
    private Long time;
    private String message;
    @Convert(converter = Contact.ConversationTYPEConverter.class,columnType = Integer.class)
private ConverSationTYPE conversationType;
    @Generated(hash = 1463103762)
    public MessageResponse(Long id, Long fromUserId, Long toUserId, Long time, String message,
            ConverSationTYPE conversationType) {
        this.id = id;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.time = time;
        this.message = message;
        this.conversationType = conversationType;
    }
    @Generated(hash = 2003436558)
    public MessageResponse() {
    }
    @Override
    public byte getCommand() {
        return Command.SEND_MESSAGE_RESPONSE;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getFromUserId() {
        return this.fromUserId;
    }
    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }
    public Long getToUserId() {
        return this.toUserId;
    }
    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }
    public Long getTime() {
        return this.time;
    }
    public void setTime(Long time) {
        this.time = time;
    }
    public String getMessage() {
        return this.message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public ConverSationTYPE getConversationType() {
        return this.conversationType;
    }
    public void setConversationType(ConverSationTYPE conversationType) {
        this.conversationType = conversationType;
    }


}
