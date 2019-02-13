package com.chat.androidclient.mvvm.viewmodel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.text.Editable
import android.text.TextWatcher
import com.blankj.utilcode.util.SPUtils
import com.chat.androidclient.R
import com.chat.androidclient.event.MessageEvent
import com.chat.androidclient.event.RefreshConversationEvent
import com.chat.androidclient.greendao.*
import com.chat.androidclient.im.ChatIM
import com.chat.androidclient.mvvm.model.Constant
import com.chat.androidclient.mvvm.model.Conversation
import com.chat.androidclient.mvvm.model.TYPE
import com.chat.androidclient.mvvm.procotol.request.SendGroupMessageRequest
import com.chat.androidclient.mvvm.procotol.request.SendMessageRequest
import com.chat.androidclient.mvvm.procotol.response.MessageResponse
import com.chat.androidclient.mvvm.view.activity.ChatActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by lps on 2018/12/28 14:00.
 */
class ChatVM(var view: ChatActivity) : BaseViewModel() {
    val type: TYPE = view.intent.getSerializableExtra(ChatActivity.TYPE) as TYPE
    
    var id: Long = view.intent.getLongExtra(ChatActivity.ID, -1)
    val devSession = DaoMaster.newDevSession(view, Constant.DBNAME)
    val msgDao = devSession.messageResponseDao
    val conversationDao = devSession.conversationDao
    private var builder: NotificationCompat.Builder? = null
    private var notification: NotificationManager? = null
    
    fun init() {
        loadMessageFromDB()
        if (!view.intent.getStringExtra(ChatActivity.MSG).isNullOrEmpty()) {
            sendMsg(view.intent.getStringExtra(ChatActivity.MSG))
        }
        val friend = devSession.contactDao.queryBuilder().where(ContactDao.Properties.Type.eq(type.id), ContactDao.Properties.UserId.eq(id)).unique()
        if (friend == null) {
            view.setConversationTitle(id.toString())
        }
        else {
            view.setConversationTitle(friend.nickname)
            
        }
    }
    
    fun loadMessageFromDB() {
        val qb = msgDao.queryBuilder()
        val condition1 = qb.and(MessageResponseDao.Properties.FromUserId.eq(id), MessageResponseDao.Properties.ToUserId.eq(getMyId()), MessageResponseDao.Properties.Type.eq(type.id))
        val condition2 = qb.and(MessageResponseDao.Properties.FromUserId.eq(getMyId()), MessageResponseDao.Properties.ToUserId.eq(id), MessageResponseDao.Properties.Type.eq(type.id))
        qb.whereOr(condition1, condition2)
        val list = qb.list()
        view.addMessages(list)
    }
    
    fun sendMsg(msg: String) {
        if (type == TYPE.PERSON)
            ChatIM.instance.cmd(SendMessageRequest(id, msg))
        else {
//             发送群消息
            ChatIM.instance.cmd(SendGroupMessageRequest(id, msg))
            
        }
//清空输入框
        view.clearInput()
//         insert db
        val message = MessageResponse()
        message.fromUserId = getMyId()
        message.message = msg
        message.toUserId = id
        message.type = type
        message.time = System.currentTimeMillis()
        msgDao.insert(message)
//         refresh list
        view.addMessage(message)
        // 最近会话列表DB 刷新这个好友
        var conversation = conversationDao.queryBuilder().where(ConversationDao.Properties.Type.eq(type.id), ConversationDao.Properties.FromId.eq(id)).unique()
        if (conversation == null) {
            conversation = Conversation()
        }
        conversation.fromId = id
        conversation.lastcontent = msg
        conversation.msgcount = 0
        conversation.type = type
        conversation.time = System.currentTimeMillis()
        conversationDao.insertOrReplace(conversation)
        
    }
    
    //收到后台推送过来的消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun ReciveMessage(event: MessageEvent) {
        val response = event.msg as MessageResponse
        
        if (response.fromUserId == SPUtils.getInstance().getLong(Constant.id)) {
        
        }
        else {
            //写入聊天消息的db
            response.type = TYPE.PERSON
            response.toUserId = SPUtils.getInstance().getLong(Constant.id)
            response.time = System.currentTimeMillis()
            msgDao.insert(response)
            //如果是当前聊天对象发给我的
            if (response.fromUserId == id) {
                //更新RecyclerView
                view.addMessage(response)
            }
            else {
                //发送通知
                notification(response)
            }
            
        }
    }
    
    fun notification(response: MessageResponse) {
        if (builder == null)
            builder = NotificationCompat.Builder(view, "recivemessage")
        builder!!.setContentTitle("收到新的消息")
                .setContentText(response.message)
                .setLargeIcon(BitmapFactory.decodeResource(view.resources, R.drawable.otherhead))
                .setSmallIcon(R.drawable.otherhead)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
        if (notification == null)
            notification = view.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChanel()
        }
        notification!!.notify(0, builder!!.build())
    }
    
    var channel: NotificationChannel? = null
    
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChanel() {
        if (channel == null) {
            channel = NotificationChannel("recivemessage", "process", NotificationManager.IMPORTANCE_LOW)
            builder!!.setOnlyAlertOnce(true)
            notification!!.createNotificationChannel(channel)
        }
    }
    
    private fun getMyId() = SPUtils.getInstance().getLong(Constant.id)
    fun getInputWatcher(): TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            if (s.length > 0) {
                view.canClickSendBtn(true)
            }
            else {
                view.canClickSendBtn(false)
                
            }
        }
        
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
        
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }
    
    override fun destroy() {
        //通知最近会话列表更新
        var conversation = conversationDao.queryBuilder().where(ConversationDao.Properties.Type.eq(type.id), ConversationDao.Properties.FromId.eq(id)).unique()
        if (conversation != null) {
            conversation.msgcount = 0
            conversationDao.insertOrReplace(conversation)
            EventBus.getDefault().post(RefreshConversationEvent())
        }
        super.destroy()
    }
}