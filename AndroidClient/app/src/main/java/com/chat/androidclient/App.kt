package com.chat.androidclient

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.chat.androidclient.im.ChatIM
import com.tencent.bugly.Bugly
import com.tencent.bugly.beta.Beta
import org.greenrobot.greendao.query.QueryBuilder

/**
 * Created by 李培生 on 2018/12/21 14:08.
 */
class App:Application() {
    companion object {
        var CONNECT=false
    }
    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        ChatIM.init()
//        LogUtils.getConfig().setBorderSwitch(false)
        QueryBuilder.LOG_SQL=true
        QueryBuilder.LOG_VALUES=true
        Bugly.init(this,"bfa8de2062",true)
    }
    
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        // 安装tinker
        Beta.installTinker();
    }
}