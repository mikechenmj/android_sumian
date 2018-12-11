package com.sumian.sd.app

import android.app.Application
import android.content.Context
import android.os.Build
import android.view.Gravity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import com.sumian.blue.manager.BlueManager
import com.sumian.common.base.BaseActivityManager
import com.sumian.common.dns.HttpDnsEngine
import com.sumian.common.dns.IHttpDns
import com.sumian.common.h5.WebViewManger
import com.sumian.common.helper.ToastHelper
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.notification.AppNotificationManager
import com.sumian.common.notification.NotificationUtil
import com.sumian.common.social.OpenEngine
import com.sumian.common.social.analytics.OpenAnalytics
import com.sumian.common.social.login.OpenLogin
import com.sumian.common.utils.SumianExecutor
import com.sumian.hw.job.SleepDataUploadManager
import com.sumian.hw.log.LogJobIntentService
import com.sumian.hw.log.LogManager
import com.sumian.hw.upgrade.model.VersionModel
import com.sumian.sd.BuildConfig
import com.sumian.sd.R
import com.sumian.sd.account.bean.Token
import com.sumian.sd.account.bean.UserInfo
import com.sumian.sd.account.login.LoginActivity
import com.sumian.sd.account.login.NewUserGuideActivity
import com.sumian.sd.account.model.AccountViewModel
import com.sumian.sd.base.ActivityDelegateFactory
import com.sumian.sd.device.DeviceManager
import com.sumian.sd.device.FileHelper
import com.sumian.sd.doctor.model.DoctorViewModel
import com.sumian.sd.kefu.KefuManager
import com.sumian.sd.log.SdLogManager
import com.sumian.sd.main.MainActivity
import com.sumian.sd.network.NetworkManager
import com.sumian.sd.network.api.SdApi
import com.sumian.sd.network.callback.BaseSdResponseCallback
import com.sumian.sd.notification.NotificationConst
import com.sumian.sd.notification.NotificationDelegate
import com.sumian.sd.notification.SchemeResolver

/**
 * Created by jzz
 * on 2018/1/15.
 * desc:
 */

object AppManager {

    private val mAccountViewModel: AccountViewModel by lazy {
        AccountViewModel(App.getAppContext())
    }

    private val mDoctorViewModel: DoctorViewModel by lazy {
        DoctorViewModel()
    }
    private val mOpenEngine: OpenEngine by lazy {
        OpenEngine.init(App.getAppContext(), BuildConfig.DEBUG, BuildConfig.UMENG_APP_KEY, BuildConfig.UMENG_CHANNEL, BuildConfig.UMENG_PUSH_SECRET)
        OpenEngine().create(App.getAppContext(), BuildConfig.DEBUG, BuildConfig.WECHAT_APP_ID, BuildConfig.WECHAT_APP_SECRET)
    }

    private val mVersionModel: VersionModel by lazy {
        VersionModel()
    }

    private val mNetworkManager: NetworkManager  by lazy {
        //注册网络引擎框架
        NetworkManager.create()
    }

    private val M_SLEEP_DATA_UPLOAD_MANAGER: SleepDataUploadManager  by lazy {
        SleepDataUploadManager(App.getAppContext())
    }

    private val mBlueManager: BlueManager by lazy {
        SumianExecutor.runOnBackgroundThread {
            val externalStorageWritable = FileHelper.init().isExternalStorageWritable
            if (externalStorageWritable) {
                FileHelper.createSDDir(FileHelper.FILE_DIR)
            }
        }
        val blueManager = BlueManager.getInstance()
        blueManager.with(App.getAppContext())
        blueManager
    }

    private val mSdApi: SdApi by lazy {
        mNetworkManager.installSdHttpRequest()
    }

    private val mHttpDns: IHttpDns? by lazy {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            //注册 aliyun httpDns
            val iHttpDns = HttpDnsEngine().init(App.getAppContext(), BuildConfig.DEBUG, BuildConfig.HTTP_DNS_ACCOUNT_ID, BuildConfig.HTTP_DNS_SECRET_KEY)
            iHttpDns.setPreHostsList(BuildConfig.BASE_URL, BuildConfig.HW_BASE_URL, BuildConfig.BASE_H5_URL)
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
    fun getAccountViewModel(): AccountViewModel {
        return mAccountViewModel
    }

    @JvmStatic
    @Synchronized
    fun getDoctorViewModel(): DoctorViewModel {
        return mDoctorViewModel
    }

    @JvmStatic
    @Synchronized
    fun getVersionModel(): VersionModel {
        return mVersionModel
    }

    @JvmStatic
    @Synchronized
    fun getSdHttpService(): SdApi {
        return mSdApi
    }

    @JvmStatic
    @Synchronized
    fun getOpenLogin(): OpenLogin {
        return mOpenEngine.openLogin
    }

    @JvmStatic
    @Synchronized
    fun getOpenAnalytics(): OpenAnalytics {
        return mOpenEngine.openAnalytics
    }

    @JvmStatic
    @Synchronized
    fun getSleepDataUploadManager(): SleepDataUploadManager {
        return M_SLEEP_DATA_UPLOAD_MANAGER
    }

    @JvmStatic
    @Synchronized
    fun getBlueManager(): BlueManager {
        return mBlueManager
    }

    @JvmStatic
    @Synchronized
    fun getHttpDns(): IHttpDns? {
        return mHttpDns
    }

    fun initOnAppStart(app: Application) {
        initUtils(app)
        BaseActivityManager.setActivityDelegateFactory(ActivityDelegateFactory())
        initAppNotificationManager(app)
        SdLogManager.init(app,
                BuildConfig.ALIYUN_LOG_ACCESS_KEY_ID,
                BuildConfig.ALIYUN_LOG_ACCESS_SECRET,
                BuildConfig.ALIYUN_LOG_PROJECT,
                BuildConfig.ALIYUN_LOG_LOG_STORE,
                BuildConfig.ALIYUN_LOG_END_POINT
        )
        observeAppLifecycle()
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


    @JvmStatic
    fun initOnFirstActivityStart(context: Context) {
        synchronized(AppManager::class.java) {
            initKefu(context)
            DeviceManager.init()
            initWebView(context)
        }
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
                NotificationConst.PUSH_CHANNEL, BuildConfig.DEBUG,
                NotificationConst.CHANNEL_ID, NotificationConst.CHANNEL_NAME,
                NotificationDelegate(), SchemeResolver, NotificationConst.USER_ID_KEY)

    }

    private fun initWebView(context: Context) {
        val webViewManger = WebViewManger.getInstance()
        webViewManger.registerX5WebView(context)
        webViewManger.setBaseUrl(BuildConfig.BASE_H5_URL)
        mHttpDns?.let {
            webViewManger.registerHttpDnsEngine(it)
        }
        webViewManger.setDebug(BuildConfig.DEBUG)
        mAccountViewModel.liveDataToken.observeForever { token ->
            webViewManger.setToken(token = token?.token)
        }
    }

    @JvmStatic
    fun initKefu(context: Context) {
        synchronized(AppManager::class) {
            KefuManager.init(context)
        }
    }

    fun exitApp() {
        AppManager.getSleepDataUploadManager().release()
        AppManager.getBlueManager().bluePeripheral?.close()
        ActivityUtils.finishAllActivities()
        LogManager.appendUserOperationLog("用户退出 app.......")
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

    fun getMainClass(): Class<MainActivity> {
        return MainActivity::class.java
    }

    fun isAppForeground(): Boolean {
        return AppUtils.isAppForeground()
    }

    // ------------ App's important lifecycle events start------------

    fun onAppForeground() {
        LogManager.appendUserOperationLog("App 进入 前台")
        DeviceManager.tryToConnectCacheMonitor()
        sendHeartbeat()
    }

    fun onAppBackground() {
        LogManager.appendUserOperationLog("App 进入 后台")
//        LogJobIntentService.uploadLogIfNeed(App.getAppContext())
    }

    fun onMainActivityCreate() {
        DeviceManager.uploadCacheSn()
        KefuManager.loginAndQueryUnreadMsg()
        AppNotificationManager.uploadPushId()
        AppManager.getSleepDataUploadManager().checkPendingTaskAndRun()
        AppManager.sendHeartbeat()
        AppManager.syncUserInfo()
    }

    fun onLoginSuccess(token: Token?) {
        if (token == null) {
            ToastUtils.showShort(R.string.error)
            return
        }
        AppManager.getAccountViewModel().updateToken(token)
        AppManager.launchMainOrNewUserGuide()
    }

    fun logoutAndLaunchLoginActivity() {
        // user report
        AppManager.getOpenAnalytics().onProfileSignOff()
        // release bluetooth
        SumianExecutor.runOnBackgroundThread { BlueManager.getInstance().doStopScan() }
        AppManager.getBlueManager().release()
        KefuManager.logout()
        // cancel notification
        NotificationUtil.cancelAllNotification(App.getAppContext())
        // update token
        AppManager.getAccountViewModel().updateToken(null)
        // update WeChat token cache
        AppManager.getOpenLogin().deleteWechatTokenCache(ActivityUtils.getTopActivity(), null)
        // finish all and start LoginActivity
        ActivityUtils.finishAllActivities()
        LoginActivity.show()
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
}
