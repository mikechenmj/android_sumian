package com.sumian.sd.app

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.Gravity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import cn.leancloud.chatkit.LCIMManager
import com.blankj.utilcode.util.*
import com.sumian.common.base.BaseActivityManager
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
import com.sumian.common.social.analytics.OpenAnalytics
import com.sumian.common.social.login.OpenLogin
import com.sumian.common.statistic.StatUtil
import com.sumian.device.callback.DeviceStatusListener
import com.sumian.device.manager.DeviceManager
import com.sumian.device.manager.upload.SleepDataUploadManager
import com.sumian.device.util.ILogger
import com.sumian.sd.BuildConfig
import com.sumian.sd.R
import com.sumian.sd.base.ActivityDelegateFactory
import com.sumian.sd.buz.account.bean.Token
import com.sumian.sd.buz.account.bean.UserInfo
import com.sumian.sd.buz.account.login.LoginActivity
import com.sumian.sd.buz.account.login.NewUserGuideActivity
import com.sumian.sd.buz.account.model.AccountManager
import com.sumian.sd.buz.cbti.video.download.VideoDownloadManager
import com.sumian.sd.buz.devicemanager.AutoSyncDeviceDataUtil
import com.sumian.sd.buz.doctor.model.DoctorViewModel
import com.sumian.sd.buz.notification.NotificationConst
import com.sumian.sd.buz.notification.NotificationDelegate
import com.sumian.sd.buz.notification.SchemeResolver
import com.sumian.sd.buz.patientdoctorim.IMManagerHost
import com.sumian.sd.buz.patientdoctorim.IMProfileProvider
import com.sumian.sd.buz.version.VersionManager
import com.sumian.sd.buz.version.ui.DeviceUpgradeDialogActivity
import com.sumian.sd.common.log.LogManager
import com.sumian.sd.common.log.SdLogManager
import com.sumian.sd.common.network.NetworkManager
import com.sumian.sd.common.network.api.SdApi
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import com.sumian.sd.common.utils.getString
import com.sumian.sd.main.MainActivity

/**
 * Created by jzz
 * on 2018/1/15.
 * desc:
 */

object AppManager {

    lateinit var mApplication: Application
    private var mAppForeground = false

    private val mDoctorViewModel: DoctorViewModel by lazy {
        DoctorViewModel()
    }
    private val mOpenEngine: OpenEngine by lazy {
        OpenEngine.init(App.getAppContext(), BuildConfig.DEBUG, BuildConfig.UMENG_APP_KEY, BuildConfig.UMENG_CHANNEL, BuildConfig.UMENG_PUSH_SECRET)
        OpenEngine().create(App.getAppContext(), BuildConfig.DEBUG, BuildConfig.WECHAT_APP_ID, BuildConfig.WECHAT_APP_SECRET)
    }

    private val mNetworkManager: NetworkManager  by lazy {
        //注册网络引擎框架
        NetworkManager.create()
    }

    private val mSdApi: SdApi by lazy {
        mNetworkManager.installSdHttpRequest()
    }

    private val mHttpDns: IHttpDns? by lazy {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            //注册 aliyun httpDns
            val iHttpDns = HttpDnsEngine().init(App.getAppContext(), BuildConfig.DEBUG, BuildConfig.HTTP_DNS_ACCOUNT_ID, BuildConfig.HTTP_DNS_SECRET_KEY)
            iHttpDns.setPreHostsList(BuildConfig.BASE_URL, BuildConfig.BASE_H5_URL)
            return@lazy iHttpDns
        } else {
            null
        }
    }

    @JvmStatic
    @Synchronized
    fun getOpenEngine(): OpenEngine {
        return mOpenEngine
    }

    @JvmStatic
    @Synchronized
    fun getAccountViewModel(): AccountManager {
        return AccountManager
    }

    @JvmStatic
    @Synchronized
    fun getDoctorViewModel(): DoctorViewModel {
        return mDoctorViewModel
    }

    @JvmStatic
    @Synchronized
    fun getSdHttpService(): SdApi {
        return mSdApi
    }

    @JvmStatic
    @Synchronized
    fun getOpenLogin(): OpenLogin {
        return mOpenEngine.openLogin!!
    }

    @JvmStatic
    @Synchronized
    fun getOpenAnalytics(): OpenAnalytics {
        return mOpenEngine.openAnalytics!!
    }

    @JvmStatic
    @Synchronized
    fun getHttpDns(): IHttpDns? {
        return mHttpDns
    }

    fun initOnAppStart(app: Application) {
        mApplication = app
        initUtils(app)
        initLeakCanary(app)
        BaseActivityManager.setActivityDelegateFactory(ActivityDelegateFactory())
        initLeanCloud()
        initAppNotificationManager(app)
        initLogManager(app)
        initStatic(app)
        observeAppLifecycle()
        observeNetworkState()
        initWebView(app)
        VideoDownloadManager.init(app)
        initDeviceManager()
    }

    private fun initKefu(app: Application) {
        KefuManager.init(app,
                KefuManager.KefuParams(
                        MainActivity::class.java,
                        getString(R.string.sleep_steward),
                        getAccountViewModel().userInfo?.avatar,
                        R.drawable.ic_notification_small)
        )
    }

    private fun initDeviceManager() {
        DeviceManager.init(application = App.getAppContext(), params = DeviceManager.Params(baseUrl = BuildConfig.BASE_URL))
        DeviceManager.setLogger(object : ILogger {
            override fun log(tag: String, log: String) {
                SdLogManager.logDevice("[$tag] $log")
                Log.d(tag, log)
            }
        })
        DeviceManager.setToken(AccountManager.tokenString)
        AccountManager.registerTokenChangeListener(object : AccountManager.TokenChangeListener {
            override fun onTokenChange(token: Token?) {
                DeviceManager.setToken(token?.token)
            }
        })
        DeviceManager.registerDeviceStatusListener(object : DeviceStatusListener {
            override fun onStatusChange(type: String) {
                when (type) {
                    DeviceManager.EVENT_RECEIVE_MONITOR_VERSION_INFO -> {
                        val compatibility = DeviceManager.checkMonitorVersionCompatibility()
                        when (compatibility) {
                            DeviceManager.PROTOCOL_VERSION_TO_HIGH -> DeviceUpgradeDialogActivity.start(DeviceUpgradeDialogActivity.TYPE_APP)
                            DeviceManager.PROTOCOL_VERSION_TO_LOW -> DeviceUpgradeDialogActivity.start(DeviceUpgradeDialogActivity.TYPE_MONITOR)
                        }
                        VersionManager.delayQueryDeviceVersion()
                    }
                    DeviceManager.EVENT_RECEIVE_SLEEP_MASTER_VERSION_INFO -> {
                        val compatibility = DeviceManager.checkSleepMasterVersionCompatibility()
                        when (compatibility) {
                            DeviceManager.PROTOCOL_VERSION_TO_HIGH -> DeviceUpgradeDialogActivity.start(DeviceUpgradeDialogActivity.TYPE_APP)
                            DeviceManager.PROTOCOL_VERSION_TO_LOW -> DeviceUpgradeDialogActivity.start(DeviceUpgradeDialogActivity.TYPE_SLEEP_MASTER)
                        }
                        VersionManager.delayQueryDeviceVersion()
                    }

                }
            }
        })
    }

    private fun initStatic(app: Application) {
        StatUtil.init(app, BuildConfig.TENCENT_STATIC_APP_ID, BuildConfig.CHANNEL, BuildConfig.DEBUG)
    }

    private fun initLeakCanary(app: Application) {
        // if (LeakCanary.isInAnalyzerProcess(app)) {
        // This process is dedicated to LeakCanary for heap analysis.
        // You should not init your app in this process.
        //     return
        // }
        //        LeakCanary.install(app)
    }

    private fun initLogManager(app: Application) {
        SdLogManager.init(app,
                BuildConfig.ALIYUN_LOG_ACCESS_KEY_ID,
                BuildConfig.ALIYUN_LOG_ACCESS_SECRET,
                BuildConfig.ALIYUN_LOG_PROJECT,
                BuildConfig.ALIYUN_LOG_LOG_STORE,
                BuildConfig.ALIYUN_LOG_END_POINT)
    }

    private fun observeAppLifecycle() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onAppForeground() {
                AppManager.onAppForeground()
                mAppForeground = true
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun onAppBackground() {
                AppManager.onAppBackground()
                mAppForeground = false
            }
        })
    }

    private fun observeNetworkState() {
        mApplication.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (NetworkUtils.getNetworkType() != NetworkUtils.NetworkType.NETWORK_NO) {
                    uploadSleepFile()
                }
            }
        }, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    private fun uploadSleepFile() {
        SleepDataUploadManager.uploadAllFile()
    }

    private fun initUtils(context: Context) {
        ToastHelper.init(context)
        Utils.init(context)
        ToastUtils.setGravity(Gravity.CENTER, 0, 0)
    }

    private fun initAppNotificationManager(app: Application) {
        AppNotificationManager.init(app,
                R.drawable.ic_notification_small, R.mipmap.ic_launcher,
                BuildConfig.LEANCLOUD_APP_ID, BuildConfig.LEANCLOUD_APP_KEY,
                NotificationDelegate(), SchemeResolver, NotificationConst.USER_ID_KEY)

    }

    private fun initLeanCloud() {
        LeanCloudManager.init(mApplication,
                BuildConfig.LEANCLOUD_APP_ID, BuildConfig.LEANCLOUD_APP_KEY,
                NotificationConst.PUSH_CHANNEL, BuildConfig.DEBUG, MainActivity::class.java)
    }

    private fun initWebView(context: Context) {
        val webViewManger = WebViewManger.getInstance()
        webViewManger.registerX5WebView(context)
        webViewManger.setBaseUrl(BuildConfig.BASE_H5_URL)
        mHttpDns?.let {
            webViewManger.registerHttpDnsEngine(it)
        }
        webViewManger.setDebug(BuildConfig.DEBUG)
        AccountManager.liveDataToken.observeForever { token ->
            webViewManger.setToken(token = token?.token)
        }
    }

    fun launchMainAndFinishAll() {
        ActivityUtils.finishAllActivities()
        launchMain()
    }

    fun launchMain() {
        ActivityUtils.startActivity(MainActivity::class.java)
    }

    fun launchMainOrNewUserGuide() {
        val token = AppManager.getAccountViewModel().token
        if (token != null && token.is_new) {
            ActivityUtils.startActivity(NewUserGuideActivity::class.java)
            ActivityUtils.finishAllActivities()
        } else {
            launchMainAndFinishAll()
        }
    }

    fun isAppForeground(): Boolean {
//        return AppUtils.isAppForeground()
        return mAppForeground
    }

    // ------------ App's important lifecycle events start------------

    fun onAppForeground() {
        LogManager.appendUserOperationLog("App 进入 前台")
        if (!DeviceManager.isMonitorConnected()) {
            DeviceManager.connectBoundDevice(null)
        }
        uploadSleepFile()
        sendHeartbeat()
        VersionManager.queryDeviceVersion(true)
        AutoSyncDeviceDataUtil.autoSyncSleepData()
    }

    fun onAppBackground() {
        LogManager.appendUserOperationLog("App 进入 后台")
    }

    fun onMainActivityCreate() {
        initKefu(mApplication)
        AppNotificationManager.uploadPushId()
        sendHeartbeat()
        syncUserInfo()
        initImManager()
    }

    private fun initImManager() {
        LCIMManager.getInstance()
                .init(mApplication,
                        BuildConfig.LEANCLOUD_APP_ID, BuildConfig.LEANCLOUD_APP_KEY,
                        getAccountViewModel().userInfo?.im_id,
                        IMProfileProvider(),
                        IMManagerHost())
    }

    fun onMainActivityRestore() {
        SdLogManager.log("on MainActivity Restore")
    }

    fun onLoginSuccess(token: Token?) {
        if (token == null) {
            ToastUtils.showShort(R.string.error)
            return
        }
        getAccountViewModel().updateToken(token)
        getAccountViewModel().updateUserInfo(token.user)
        launchMainOrNewUserGuide()
        StatUtil.reportAccount(token.user.mobile, token.expired_at.toLong())
        sendHeartbeat()
    }

    fun logoutAndLaunchLoginActivity() {
        DeviceManager.disconnect()
        // user report
        AppManager.getOpenAnalytics().onProfileSignOff()
        // release bluetooth
        KefuManager.logout()
        // cancel notification
        NotificationUtil.cancelAllNotification(App.getAppContext())
        // update token
        getAccountViewModel().clearToken()
        // update WeChat token cache
        AppManager.getOpenLogin().deleteWechatTokenCache(ActivityUtils.getTopActivity(), null)
        // finish all and start LoginActivity
        ActivityUtils.finishAllActivities()
        LoginActivity.show()
        StatUtil.removeAccount()
        LCIMManager.getInstance().close()
    }

    // ------------ App's important lifecycle events end------------

    fun sendHeartbeat() {
        if (!getAccountViewModel().isLogin) {
            return
        }
        AppManager.getSdHttpService().sendHeartbeats("open_app")
                .enqueue(object : BaseSdResponseCallback<Any?>() {
                    override fun onFailure(errorResponse: ErrorResponse) {

                    }

                    override fun onSuccess(response: Any?) {

                    }
                })
    }

    fun syncUserInfo() {
        val call = AppManager.getSdHttpService().getUserProfile()
        call.enqueue(object : BaseSdResponseCallback<UserInfo>() {
            override fun onFailure(errorResponse: ErrorResponse) {
            }

            override fun onSuccess(response: UserInfo?) {
                AppManager.getAccountViewModel().updateUserInfo(response)
            }
        })
    }

    fun exitApp() {
        DeviceManager.disconnect()
        ActivityUtils.finishAllActivities()
    }
}
