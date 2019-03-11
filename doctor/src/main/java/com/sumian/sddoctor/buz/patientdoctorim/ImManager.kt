package com.sumian.sddoctor.buz.patientdoctorim

import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.SaveCallback
import com.avos.avoscloud.im.v2.*
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback
import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage
import com.blankj.utilcode.util.LogUtils
import com.sumian.sddoctor.app.AppManager
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage


/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/11 10:21
 * desc   :
 * version: 1.0
 */
object ImManager {
    private lateinit var mImId:String

    fun init(imId:String) {
        mImId = imId
    }


    fun test() {
        val avObject = AVObject("test obj")
        avObject.put("title", "hello world")
        avObject.saveInBackground(object : SaveCallback() {
            override fun done(p0: AVException?) {
                LogUtils.d("av save")
            }
        })
    }

    val mClient: AVIMClient by lazy {
        AVIMClient.getInstance(mImId)
    }

    fun open(callback: AVIMClientCallback) {
        mClient.open(callback)
    }

    fun sendTextMessage(conversation: AVIMConversation, message: String, callback: AVIMConversationCallback) {
        val msg = AVIMTextMessage()
        msg.text = message
        conversation.sendMessage(msg, callback)
    }

    fun sendImageMessage(conversation: AVIMConversation, url: String, callback: AVIMConversationCallback) {
        val msg = AVIMImageMessage()
        msg.content = url
        conversation.sendMessage(msg, callback)
    }

    fun sendAudioMessage(conversation: AVIMConversation, url: String, callback: AVIMConversationCallback) {
        val msg = AVIMAudioMessage()
        msg.content = url
        conversation.sendMessage(msg, callback)
    }

    fun getConverstion(conversationId: String): AVIMConversation? {
        return mClient.getConversation(conversationId)
    }

    class CustomConversationEventHandler : AVIMConversationEventHandler() {
        override fun onInvited(p0: AVIMClient?, p1: AVIMConversation?, p2: String?) {
        }

        override fun onMemberJoined(p0: AVIMClient?, p1: AVIMConversation?, p2: MutableList<String>?, p3: String?) {
        }

        override fun onKicked(p0: AVIMClient?, p1: AVIMConversation?, p2: String?) {
        }

        override fun onMemberLeft(p0: AVIMClient?, p1: AVIMConversation?, p2: MutableList<String>?, p3: String?) {
        }
    }


    fun setConversationEventHandler() {
        AVIMMessageManager.setConversationEventHandler(object : AVIMConversationEventHandler() {
            override fun onInvited(p0: AVIMClient?, p1: AVIMConversation?, p2: String?) {
            }

            override fun onMemberJoined(p0: AVIMClient?, p1: AVIMConversation?, p2: MutableList<String>?, p3: String?) {
            }

            override fun onKicked(p0: AVIMClient?, p1: AVIMConversation?, p2: String?) {
            }

            override fun onMemberLeft(p0: AVIMClient?, p1: AVIMConversation?, p2: MutableList<String>?, p3: String?) {
            }
        })

    }

    fun registerDefaultMessageHandler() {
        AVIMMessageManager.registerDefaultMessageHandler(object : AVIMMessageHandler() {
            override fun onMessage(message: AVIMMessage?, conversation: AVIMConversation?, client: AVIMClient?) {
                super.onMessage(message, conversation, client)
                conversation?.conversationId
            }

            override fun onMessageReceipt(message: AVIMMessage?, conversation: AVIMConversation?, client: AVIMClient?) {
                super.onMessageReceipt(message, conversation, client)
            }
        })
    }

}