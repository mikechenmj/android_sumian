@file:Suppress("UNUSED_ANONYMOUS_PARAMETER")

package com.sumian.sd.buz.kefu

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.View
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
import com.sumian.common.statistic.StatUtil
import com.sumian.common.utils.SumianExecutor
import com.sumian.sd.BuildConfig
import com.sumian.sd.R
import com.sumian.sd.app.App
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.account.bean.UserInfo
import com.sumian.sd.buz.stat.StatConstants
import com.sumian.sd.common.network.callback.BaseSdResponseCallback

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
        //注册相关provider
        registerAccountProvider()
        registerUserProfileProvider()
        ActivityUtils.startActivity(getChatRoomLaunchIntent())
        StatUtil.trackBeginPage(App.getAppContext(), StatConstants.page_sleep_steward)
        StatUtil.event(StatConstants.enter_sleep_steward_page)
        mLaunchKefuActivity = true
    }

    fun init(context: Context) {
        val options = ChatClient.Options()
        options.setConsoleLog(BuildConfig.DEBUG)
        options.setAppkey(BuildConfig.EASEMOB_APP_KEY)//必填项，appkey获取地址：kefu.easemob.com，“管理员模式 > 渠道管理 > 手机APP”页面的关联的“AppKey”
        options.setTenantId(BuildConfig.EASEMOB_TENANT_ID)//必填项，tenantId获取地址：kefu.easemob.com，“管理员模式 > 设置 > 企业信息”页面的“租户ID”
        ChatClient.getInstance().init(context, options)
        // Kefu EaseUI的初始化
        UIProvider.getInstance().init(context)
        registerMessageListener()
        UIProvider.getInstance().helloWord = App.getAppContext().getString(R.string.doctor_say_hello)
        UIProvider.getInstance().setUnreadMessageChangeListener { mMessageCountLiveData.postValue(it) }
    }

    fun logout() {
        // logout chat
        ChatClient.getInstance().logout(true, object : Callback {
            override fun onSuccess() {
                UIProvider.getInstance().isLogin = false
            }

            override fun onProgress(progress: Int, status: String?) {
            }

            override fun onError(code: Int, error: String?) {
            }
        })
    }

    fun loginAndQueryUnreadMsg() {
        loginEasemob(null)
    }

    private fun loginEasemob(loginCallback: LoginCallback?) {
        val userInfo = AppManager.getAccountViewModel().userInfo ?: return
        val imId = userInfo.getIm_id()
        val md5Pwd = userInfo.getIm_password()
        if (TextUtils.isEmpty(imId) || TextUtils.isEmpty(md5Pwd)) {//可能账号未在环信注册，提醒后台去注册环信账号
            UIProvider.getInstance().isLogin = false
            val notifyCount = 0
            notifyServerRegister2ImServer(userInfo, notifyCount)
            return
        }
        val instance = ChatClient.getInstance()
        instance?.let {
            val loggedInBefore = it.isLoggedInBefore
            if (loggedInBefore) {
                SumianExecutor.runOnUiThread({
                    UIProvider.getInstance().isLogin = true
                    loginCallback?.onSuccess()
                    UIProvider.getInstance().onLoginCallback?.onLoginSuccess()
                    // Log.e("TAG", "onSuccess: ----kefu ime----->")
                })
            } else {
                it.login(imId, md5Pwd, object : Callback {
                    override fun onSuccess() {
                        SumianExecutor.runOnUiThread({
                            UIProvider.getInstance().isLogin = true
                            loginCallback?.onSuccess()
                            UIProvider.getInstance().onLoginCallback?.onLoginSuccess()
                            // Log.e("TAG", "onSuccess: ----kefu ime----->")
                        })
                    }

                    override fun onError(code: Int, error: String) {
                        SumianExecutor.runOnUiThread({
                            when (code) {
                                Error.USER_ALREADY_LOGIN -> {
                                    UIProvider.getInstance().isLogin = true
                                    loginCallback?.onSuccess()
                                    UIProvider.getInstance().onLoginCallback?.onLoginSuccess()
                                }
                                else -> {
                                    UIProvider.getInstance().isLogin = false
                                    loginCallback?.onFailed(error)
                                    val notifyCount = 0
                                    notifyServerRegister2ImServer(userInfo, notifyCount)
                                    UIProvider.getInstance().onLoginCallback?.onLoginFailed()
                                }
                            }
                            Log.e("TAG", "code=$code  error=$error")
                        })
                    }

                    override fun onProgress(progress: Int, status: String) {
                        LogUtils.d(progress)
                    }
                })
            }
        }
    }

    private fun notifyServerRegister2ImServer(userInfo: UserInfo, notifyCount: Int) {
        val call = AppManager.getSdHttpService().notifyRegisterImServer(userInfo.id)
        call.enqueue(object : BaseSdResponseCallback<KeFuMessage>() {
            override fun onSuccess(response: KeFuMessage?) {
                response?.let {
                    if (it.isRegisterOk()) {
                        loginAndQueryUnreadMsg()
                    } else {
                        retryRegister(notifyCount, userInfo)
                    }
                }
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                when (errorResponse.code) {
                    KeFuMessage.REPEAT_ERROR -> {
                        loginAndQueryUnreadMsg()
                    }
                    KeFuMessage.OTHER_ERROR -> {
                        retryRegister(notifyCount, userInfo)
                    }
                }
            }
        })
    }

    private fun retryRegister(notifyCount: Int, userInfo: UserInfo): Any {
        return if (notifyCount <= 2) {
            notifyServerRegister2ImServer(userInfo, (notifyCount + 1))
        } else {
            Log.e("TAG", "onFailure: -------->产品要求：通知服务器注册3次失败，不管")
        }
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

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    private fun getChatRoomLaunchIntent(): Intent {
        val userInfo = AppManager.getAccountViewModel().userInfo
        val visitorInfo = ContentFactory.createVisitorInfo(null)
                .nickName(userInfo.getNickname())
                .name(userInfo.getName())
                .phone(userInfo.getMobile())
                .description("患者APP")
        return IntentBuilder(App.getAppContext())
                .setServiceIMNumber(BuildConfig.EASEMOB_CUSTOMER_SERVICE_ID)
                .setShowUserNick(false)
                .setVisitorInfo(visitorInfo).build()
    }

    private fun registerAccountProvider() {
        UIProvider.getInstance().setAccountProvider { tvLoginStateTips, chatTitleBar, messageList ->
            loginEasemob(object : LoginCallback {
                override fun onSuccess() {
                    SumianExecutor.runOnUiThread({
                        UIProvider.getInstance().isLogin = true
                        tvLoginStateTips.visibility = View.GONE
                        messageList.registerWelcomeMsg()
                        chatTitleBar.hideLoading()
                    })
                }

                override fun onFailed(error: String) {
                    super.onFailed(error)
                    SumianExecutor.runOnUiThread({
                        UIProvider.getInstance().isLogin = false
                        tvLoginStateTips.visibility = View.VISIBLE
                        chatTitleBar.hideLoading()
                    })
                }
            })
        }
    }

    private fun registerUserProfileProvider() {
        UIProvider.getInstance().setUserProfileProvider { context, message, userAvatarView, usernickView ->
            if (Message.Direct.SEND == message.direct()) {
                userAvatarView?.let {
                    ImageLoader.loadImage(AppManager.getAccountViewModel().userInfo.getAvatar(), userAvatarView, R.mipmap.ic_chat_right_default, R.mipmap.ic_chat_right_default)
                }
            }
        }
    }

    interface LoginCallback {
        fun onSuccess()
        fun onFailed(error: String) {
            LogUtils.d(error)
        }
    }
}