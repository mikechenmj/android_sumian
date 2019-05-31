package com.sumian.sddoctor.app

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import cn.leancloud.chatkit.LCIMManager
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.Utils
import com.sumian.common.base.BaseActivityManager
import com.sumian.common.buz.async.AsyncCallback
import com.sumian.common.buz.kefu.KefuManager
import com.sumian.common.dns.HttpDnsEngine
import com.sumian.common.dns.IHttpDns
import com.sumian.common.h5.WebViewManger
import com.sumian.common.helper.ToastHelper
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.notification.AppNotificationManager
import com.sumian.common.notification.LeanCloudManager
import com.sumian.common.notification.NotificationUtil
import com.sumian.common.social.OpenEngine
import com.sumian.common.social.login.OpenLogin
import com.sumian.common.statistic.StatUtil
import com.sumian.sddoctor.BuildConfig
import com.sumian.sddoctor.R
import com.sumian.sddoctor.account.AccountViewModel
import com.sumian.sddoctor.base.ActivityDelegateFactory
import com.sumian.sddoctor.buz.patientdoctorim.IMManagerHost
import com.sumian.sddoctor.buz.patientdoctorim.IMProfileProvider
import com.sumian.sddoctor.log.SddLogManager
import com.sumian.sddoctor.login.login.ChooseIdentityActivity
import com.sumian.sddoctor.login.login.LoginActivity
import com.sumian.sddoctor.login.login.bean.DoctorInfo
import com.sumian.sddoctor.login.login.bean.LoginResponse
import com.sumian.sddoctor.main.MainActivity
import com.sumian.sddoctor.network.NetApi
import com.sumian.sddoctor.network.NetworkManager
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.notification.NotificationConst
import com.sumian.sddoctor.notification.NotificationDelegate
import com.sumian.sddoctor.notification.SchemeResolver

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/14 9:29
 *     desc   :
 *     version: 1.0
 * </pre>
 */
object AppManager {
    private lateinit var mApplication: Application

    private val mAccountViewModel: AccountViewModel by lazy {
        AccountViewModel(App.getAppContext())
    }

    private val mHttpService: NetApi by lazy {
        NetworkManager.create().installSddHttpRequest()
    }

    private val mOpenEngine: OpenEngine by lazy {
        LogUtils.d("create open login")
        OpenEngine().create(App.getAppContext(), BuildConfig.DEBUG, BuildConfig.WECHAT_APP_ID, BuildConfig.WECHAT_APP_SECRET)
    }

    private val mHttpDns: IHttpDns? by lazy {
        //注册 aliyun httpDns
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            val iHttpDns = HttpDnsEngine().init(App.getAppContext(), false, BuildConfig.HTTP_DNS_ACCOUNT_ID, BuildConfig.HTTP_DNS_SECRET_KEY)
            iHttpDns.setPreHostsList(BuildConfig.BASE_URL, BuildConfig.BASE_H5_URL)
            return@lazy iHttpDns
        } else {
            return@lazy null
        }
    }

    @JvmStatic
    @Synchronized
    fun getAccountViewModel(): AccountViewModel {
        return mAccountViewModel
    }

    @JvmStatic
    @Synchronized
    fun getHttpService(): NetApi {
        return mHttpService
    }

    @JvmStatic
    @Synchronized
    fun getOpenEngine(): OpenEngine {
        return mOpenEngine
    }

    fun getOpenLogin(): OpenLogin {
        return mOpenEngine.openLogin!!
    }

    @JvmStatic
    @Synchronized
    fun getHttpDns(): IHttpDns? {
        return mHttpDns
    }

    fun init(application: Application) {
        mApplication = application
        Utils.init(application)
        observeTokenInvalidation()
        ToastHelper.init(application)
        OpenEngine.init(application, BuildConfig.DEBUG, BuildConfig.UMENG_APP_KEY, BuildConfig.UMENG_CHANNEL, BuildConfig.UMENG_PUSH_SECRET)
        initLeanCloud()
        initNotification(application)
        initWebView(application)
        initStatic(application)
        BaseActivityManager.setActivityDelegateFactory(ActivityDelegateFactory())
        observeAppLifecycle()
        SddLogManager.init(application,
                BuildConfig.ALIYUN_LOG_ACCESS_KEY_ID,
                BuildConfig.ALIYUN_LOG_ACCESS_SECRET,
                BuildConfig.ALIYUN_LOG_PROJECT,
                BuildConfig.ALIYUN_LOG_LOG_STORE,
                BuildConfig.ALIYUN_LOG_END_POINT
        )
    }

    private fun initLeanCloud() {
        LeanCloudManager.init(mApplication,
                BuildConfig.LEANCLOUD_APP_ID, BuildConfig.LEANCLOUD_APP_KEY,
                NotificationConst.PUSH_CHANNEL, BuildConfig.DEBUG, MainActivity::class.java)
    }

    private fun initStatic(app: Application) {
        StatUtil.init(app, BuildConfig.TENCENT_STATIC_APP_ID, BuildConfig.CHANNEL, BuildConfig.DEBUG)
    }

    private fun observeAppLifecycle() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onAppForeground() {
                AppManager.onAppForeground()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun onAppBackground() {
                AppManager.onAppBackground()
            }
        })
    }

    private fun initNotification(app: Application) {
        AppNotificationManager.init(app,
                R.drawable.ic_notification_small, R.mipmap.ic_launcher,
                NotificationConst.CHANNEL_ID, NotificationConst.CHANNEL_NAME,
                NotificationDelegate(), SchemeResolver, NotificationConst.USER_ID_KEY)
    }

    private fun observeTokenInvalidation() {
        getAccountViewModel().getLiveDataTokenInvalidState()
                .observeForever { t ->
                    if (t != null && t) {
                        ActivityUtils.startActivity(LoginActivity::class.java)
                    }
                }
    }

    private fun initWebView(context: Context) {
        val webViewManger = WebViewManger.getInstance()
        webViewManger.registerX5WebView(context)
        webViewManger.setBaseUrl(BuildConfig.BASE_H5_URL)
        mHttpDns?.let {
            webViewManger.registerHttpDnsEngine(it)
        }
        webViewManger.setDebug(BuildConfig.DEBUG)
        mAccountViewModel.getTokenInfo().observeForever { token ->
            webViewManger.setToken(token = token?.token)
        }
    }

    fun updateDoctorInfo(callback: AsyncCallback<DoctorInfo?>? = null) {
        AppManager.getHttpService()
                .getDoctorInfo()
                .enqueue(object : BaseSdResponseCallback<DoctorInfo>() {
                    override fun onFailure(errorResponse: ErrorResponse) {
                        LogUtils.d(errorResponse.message)
                        callback?.onFailed(errorResponse.code, errorResponse.message)
                    }

                    override fun onSuccess(response: DoctorInfo?) {
                        AppManager.getAccountViewModel().updateDoctorInfo(response)
                        callback?.onSuccess(response)
                    }

                    override fun onFinish() {
                        super.onFinish()
                        callback?.onFinish()
                    }
                })
    }

    // ------------ App's important lifecycle events start------------

    fun onAppForeground() {
        if (AppManager.getAccountViewModel().getToken() != null) {
            updateDoctorInfo()
        }
        sendHeartbeat()
    }

    fun onAppBackground() {
    }

    fun onMainActivityCreate() {
        initKefu(mApplication)
        AppNotificationManager.uploadPushId()
        updateDoctorInfo()
        initImManager()
    }

    private fun initImManager() {
        LCIMManager.getInstance().init(mApplication,
                BuildConfig.LEANCLOUD_APP_ID, BuildConfig.LEANCLOUD_APP_KEY,
                getAccountViewModel().getDoctorInfo().value!!.imId,
                IMProfileProvider(),
                IMManagerHost())
    }

    fun onLoginSuccess(loginResponse: LoginResponse?, isNewRegister: Boolean = false) {
        AppManager.getAccountViewModel().updateTokenInfoAndDoctorInfo(loginResponse)
        ActivityUtils.finishAllActivities()
        if (loginResponse?.is_new == true) {
            ActivityUtils.startActivity(ChooseIdentityActivity::class.java)
        } else {
            ActivityUtils.startActivity(ChooseIdentityActivity::class.java) // todo remove
            ActivityUtils.startActivity(MainActivity::class.java)
        }
        sendHeartbeat()
    }

    fun onLogout() {
        KefuManager.logout()
        LCIMManager.getInstance().close()
        NotificationUtil.cancelAllNotification(App.getAppContext())
    }

    @JvmStatic
    fun initKefu(context: Context) {
        KefuManager.init(mApplication,
                KefuManager.KefuParams(
                        MainActivity::class.java,
                        context.resources.getString(R.string.sleep_steward),
                        getAccountViewModel().getDoctorInfo().value?.avatar,
                        R.drawable.ic_notification_small))
    }

    // ------------ App's important lifecycle events end  ------------


    fun sendHeartbeat() {
        if (!getAccountViewModel().isLogin()) {
            return
        }
        AppManager.getHttpService().sendHeartbeats("open_app")
                .enqueue(object : BaseSdResponseCallback<Any?>() {
                    override fun onFailure(errorResponse: ErrorResponse) {

                    }

                    override fun onSuccess(response: Any?) {

                    }
                })
    }
}