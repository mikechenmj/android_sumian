package com.sumian.sd.kefu

import android.content.Intent
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.hyphenate.chat.ChatClient
import com.hyphenate.chat.ChatManager
import com.hyphenate.chat.Message
import com.hyphenate.helpdesk.Error
import com.hyphenate.helpdesk.callback.Callback
import com.hyphenate.helpdesk.easeui.UIProvider
import com.hyphenate.helpdesk.easeui.util.IntentBuilder
import com.hyphenate.helpdesk.model.ContentFactory
import com.sumian.common.image.ImageLoader
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.BuildConfig
import com.sumian.sd.R
import com.sumian.sd.app.App
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
    var mMessageCountLiveData = MutableLiveData<Int>()

    @JvmStatic
    fun launchKefuActivity() {
        loginEasemob(object : LoginCallback {
            override fun onSuccess() {
                UIProvider.getInstance().clearCacheMsg()
                ActivityUtils.startActivity(getChatRoomLaunchIntent())
            }
        })
        mLaunchKefuActivity = true
    }

    init {
        registerMessageListener()
        UIProvider.getInstance().setUnreadMessageChangeListener { mMessageCountLiveData.postValue(it) }
    }

    fun loginAndQueryUnreadMsg() {
        loginEasemob(object : LoginCallback {
            override fun onSuccess() {
            }
        })
    }

    private fun loginEasemob(loginCallback: LoginCallback?) {
        val userInfo = AppManager.getAccountViewModel().userInfo ?: return
        val imId = userInfo.getIm_id()
        val md5Pwd = userInfo.getIm_password()
        if (TextUtils.isEmpty(imId) || TextUtils.isEmpty(md5Pwd)) return
        ChatClient.getInstance().login(imId, md5Pwd, object : Callback {
            override fun onSuccess() {
                loginCallback?.onSuccess()
            }

            override fun onError(code: Int, error: String) {
                if (code == Error.USER_ALREADY_LOGIN) {
                    loginCallback?.onSuccess()
                } else {
                    loginCallback?.onFailed(error)
                }
                LogUtils.d(error)
            }

            override fun onProgress(progress: Int, status: String) {
                LogUtils.d(progress)
            }
        })
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

    private fun getChatRoomLaunchIntent(): Intent {
        val visitorInfo = ContentFactory.createVisitorInfo(null)
                .nickName(AppManager.getAccountViewModel().userInfo!!.getNickname())
                .name(AppManager.getAccountViewModel().userInfo!!.getNickname())
                .phone(AppManager.getAccountViewModel().userInfo!!.getMobile())
        UIProvider.getInstance().setUserProfileProvider { context, message, userAvatarView, usernickView ->
            if (Message.Direct.SEND == message.direct()) {
                ImageLoader.loadImage(AppManager.getAccountViewModel().userInfo!!.getAvatar(), userAvatarView, R.mipmap.ic_chat_right_default, R.mipmap.ic_chat_right_default)
            }
        }
        return IntentBuilder(App.getAppContext())
                .setServiceIMNumber(BuildConfig.EASEMOB_CUSTOMER_SERVICE_ID)
                .setShowUserNick(false)
                .setVisitorInfo(visitorInfo).build()
    }

    interface LoginCallback {
        fun onSuccess()
        fun onFailed(error: String) {
            LogUtils.d(error)
        }
    }
}