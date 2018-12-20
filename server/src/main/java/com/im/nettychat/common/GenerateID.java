/*
 * Project: com.im.nettychat.common
 * 
 * File Created at 2018/12/20
 * 
 * Copyright 2018 CMCC Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * ZYHY Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license.
 */
package com.im.nettychat.common;

import static com.im.nettychat.cache.CacheName.USER_ID;
import static com.im.nettychat.service.RedisService.redisService;

/**
 * @author hejianglong
 * @Desc
 * @date 2018/12/20 下午9:43
 */
public class GenerateID {

    public static Long generateID() {
        return redisService.vIncr(USER_ID);
    }
}
