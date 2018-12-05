package com.sumian.sd.kefu

import com.blankj.utilcode.util.LogUtils
import com.hyphenate.chat.ChatClient
import com.hyphenate.chat.ChatManager
import com.hyphenate.chat.Message
import com.sumian.common.network.response.ErrorResponse
import com.sumian.hw.leancloud.HwLeanCloudHelper
import com.sumian.sd.app.AppManager
import com.sumian.sd.network.callback.BaseSdResponseCallback

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/30 15:52
 *     desc   :
 *     version: 1.0
 * </pre>
 */
object KefuManager {
    /**
     * 每次打开客服页面，发送第一条消息时需要给服务器发送通知
     */
    var mLaunchKefuActivity = false

    @JvmStatic
    fun launchKefuActivity() {
        HwLeanCloudHelper.loginEasemob { HwLeanCloudHelper.startEasemobChatRoom() }
        mLaunchKefuActivity = true
    }

    init {
        registerMessageListener()
    }

    /**
     * 需求：用户进入客服页面发送第一条消息时要上报服务器
     */
    private fun registerMessageListener() {
        ChatClient.getInstance().chatManager().addMessageListener(object : ChatManager.MessageListener {
            override fun onMessage(msgs: MutableList<Message>?) {
            }

            override fun onMessageSent() {
                if (mLaunchKefuActivity) {
                    notifyUserSendFirstMessage()
                }
            }

            override fun onCmdMessage(msgs: MutableList<Message>?) {
            }

            override fun onMessageStatusUpdate() {
            }
        })
    }

    private fun notifyUserSendFirstMessage() {
        AppManager
                .getSdHttpService()
                .newCustomerMessage()
                .enqueue(object : BaseSdResponseCallback<Any>() {
                    override fun onFailure(errorResponse: ErrorResponse) {
                        LogUtils.d(errorResponse.message)
                    }

                    override fun onSuccess(response: Any?) {
                        LogUtils.d(response)
                    }
                })
        mLaunchKefuActivity = false
    }
}