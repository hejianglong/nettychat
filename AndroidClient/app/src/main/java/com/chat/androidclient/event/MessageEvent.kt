package com.chat.androidclient.event

import com.chat.androidclient.mvvm.model.PacketResponse

/**
 * Created by lps on 2018/12/27 10:57.
 * 收到信息回调
 */
class MessageEvent(var msg: PacketResponse)  {
}