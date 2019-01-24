package com.chat.androidclient.mvvm.viewmodel

import android.databinding.ObservableField
import com.chat.androidclient.event.AddFriendResponseEvent
import com.chat.androidclient.greendao.DaoMaster
import com.chat.androidclient.greendao.DaoSession
import com.chat.androidclient.im.ChatIM
import com.chat.androidclient.mvvm.model.Constant
import com.chat.androidclient.mvvm.model.Friend
import com.chat.androidclient.mvvm.model.User
import com.chat.androidclient.mvvm.procotol.request.AddFriendRequest
import com.chat.androidclient.mvvm.view.activity.ChatActivity
import com.chat.androidclient.mvvm.view.activity.FriendDetailActivity
import com.chat.androidclient.mvvm.view.custom.LoadingDialog
import org.greenrobot.eventbus.Subscribe

/**
 * Created by lps on 2019/1/8 11:00.
 */
class FriendDetailVM(var view: FriendDetailActivity) : BaseViewModel() {
    var user: ObservableField<User> = ObservableField()
    
    fun init() {
       
        user.set( view.intent.getSerializableExtra(Constant.FRIENDDETAIL_USER_INFO) as User)
    }
    
    var dialog: LoadingDialog? = null
    fun addFriend() {
        if (dialog == null) {
            dialog = LoadingDialog(view)
        }
        dialog?.show()
        val request = AddFriendRequest(user.get()?.id)
        ChatIM.instance.cmd(request)
    }
    
    @Subscribe
    fun addFriendEvent(event: AddFriendResponseEvent) {
        dialog?.dismiss()
        if (event.msg.error) {
            view.showMsg(event.msg.errorInfo)
        }
        else {
            val session = DaoMaster.newDevSession(view, Constant.DBNAME)
            val friend = Friend()
            friend.userId=user.get()!!.id
            friend.nickname=user.get()?.username
            session.friendDao.insertOrReplace(friend)
            ChatActivity.startActivity(view, user.get()!!.id!!,"我们已经是好友了，快来聊天吧！！")
            view.finish()
        }
    }

    
    override fun destroy() {
        super.destroy()
        if (dialog != null && dialog!!.isShowing) {
            dialog?.dismiss()
        }
    }
}