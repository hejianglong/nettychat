/*
 * Project: com.im.nettychat.protocol.response
 * 
 * File Created at 2018/12/21
 * 
 * Copyright 2018 CMCC Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * ZYHY Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.im.nettychat.protocol.response;

import com.im.nettychat.protocol.ResponsePacket;
import lombok.Data;
import static com.im.nettychat.common.Command.REGISTER_RESPONSE;

/**
 * @author hejianglong
 * @Desc
 * @date 2018/12/21 下午6:35
 */
@Data
public class RegisterResponse extends ResponsePacket {

    private Long userId;

    private String name;

    private String icon;

    private String desc;

    @Override
    public Byte getCommand() {
        return REGISTER_RESPONSE;
    }
}

