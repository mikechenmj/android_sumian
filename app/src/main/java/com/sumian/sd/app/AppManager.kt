package com.sumian.sd.app

import android.content.Context
import android.os.Build
import android.view.Gravity
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import com.hyphenate.chat.ChatClient
import com.hyphenate.helpdesk.easeui.UIProvider
import com.sumian.blue.manager.BlueManager
import com.sumian.common.base.BaseActivityManager
import com.sumian.common.dns.HttpDnsEngine
import com.sumian.common.dns.IHttpDns
import com.sumian.common.h5.WebViewManger
import com.sumian.common.helper.ToastHelper
import com.sumian.common.operator.AppOperator
import com.sumian.common.social.OpenEngine
import com.sumian.common.social.analytics.OpenAnalytics
import com.sumian.common.social.login.OpenLogin
import com.sumian.hw.job.SleepDataUploadManager
import com.sumian.hw.leancloud.HwLeanCloudHelper
import com.sumian.hw.upgrade.model.VersionModel
import com.sumian.sd.BuildConfig
import com.sumian.sd.account.model.AccountViewModel
import com.sumian.sd.base.ActivityDelegateFactory
import com.sumian.sd.device.DeviceManager
import com.sumian.sd.device.FileHelper
import com.sumian.sd.doctor.model.DoctorViewModel
import com.sumian.sd.leancloud.LeanCloudManager
import com.sumian.sd.network.NetworkManager
import com.sumian.sd.network.api.HwApi
import com.sumian.sd.network.api.SdApi

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
        AppOperator.runOnThread {
            val externalStorageWritable = FileHelper.init().isExternalStorageWritable
            if (externalStorageWritable) {
                FileHelper.createSDDir(FileHelper.FILE_DIR)
            }
        }
        val blueManager = BlueManager.init()
        blueManager.with(App.getAppContext())
        blueManager
    }

    private val mSdApi: SdApi by lazy {
        mNetworkManager.installSdHttpRequest()
    }
    private val mHwApi: HwApi by lazy {
        mNetworkManager.installHwHttpRequest()
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
    fun getHwHttpService(): HwApi {
        return mHwApi
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

    fun initOnAppStart() {
        BaseActivityManager.setActivityDelegateFactory(ActivityDelegateFactory())
    }

    @JvmStatic
    fun initOnFirstActivityStart(context: Context) {
        synchronized(AppManager::class.java) {
            DeviceManager.init()
            initUtils(context)
            initLeanCloud(context)
            HwLeanCloudHelper.init(context)
            initKefu(context)
            initWebView()
        }
    }

    private fun initLeanCloud(context: Context) {
        LeanCloudManager.init(context)
    }

    private fun initUtils(context: Context) {
        ToastHelper.init(context)
        Utils.init(context)
        ToastUtils.setGravity(Gravity.CENTER, 0, 0)
    }

    private fun initWebView() {
        val webViewManger = WebViewManger.getInstance()
        webViewManger.setBaseUrl(BuildConfig.BASE_H5_URL)
        mHttpDns?.let {
            webViewManger.registerHttpDnsEngine(it)
        }
        webViewManger.setDebug(BuildConfig.DEBUG)
        mAccountViewModel.liveDataToken.observeForever { token -> webViewManger.setToken(token?.token) }
    }

    private fun initKefu(context: Context) {
        // Kefu SDK 初始化
        val options = ChatClient.Options()
        options.setConsoleLog(BuildConfig.DEBUG)
        options.setAppkey(BuildConfig.EASEMOB_APP_KEY)//必填项，appkey获取地址：kefu.easemob.com，“管理员模式 > 渠道管理 > 手机APP”页面的关联的“AppKey”
        options.setTenantId(BuildConfig.EASEMOB_TENANT_ID)//必填项，tenantId获取地址：kefu.easemob.com，“管理员模式 > 设置 > 企业信息”页面的“租户ID”
        if (!ChatClient.getInstance().init(context, options)) {
            return
        }
        // Kefu EaseUI的初始化
        UIProvider.getInstance().init(context)
    }
}
