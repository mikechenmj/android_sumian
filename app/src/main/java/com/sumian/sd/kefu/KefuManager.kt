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
class KefuManager private constructor() {
    /**
     * 每次打开客服页面，发送第一条消息时需要给服务器发送通知
     */
    var waitingSendMessage = false

    companion object {

        private val INSTANCE: KefuManager by lazy {
            KefuManager()
        }

        @JvmStatic
        fun getInstance(): KefuManager {
            return INSTANCE
        }

        @JvmStatic
        fun launchKefuActivity() {
            HwLeanCloudHelper.loginEasemob { HwLeanCloudHelper.startEasemobChatRoom() }
            INSTANCE.waitingSendMessage = true
        }
    }

    init {
        registerMessageListener()
    }

    private fun registerMessageListener() {
        ChatClient.getInstance().chatManager().addMessageListener(object : ChatManager.MessageListener {
            override fun onMessage(msgs: MutableList<Message>?) {
            }

            override fun onMessageSent() {
                if (waitingSendMessage) {
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
                    waitingSendMessage = false
                }
            }

            override fun onCmdMessage(msgs: MutableList<Message>?) {
            }

            override fun onMessageStatusUpdate() {
            }
        })
    }
}