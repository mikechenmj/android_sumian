package com.sumian.sd.app;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.text.emoji.EmojiCompat;
import android.support.text.emoji.bundled.BundledEmojiCompatConfig;
import android.view.Gravity;

import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.helpdesk.easeui.UIProvider;
import com.sumian.blue.manager.BlueManager;
import com.sumian.common.dns.HttpDnsEngine;
import com.sumian.common.dns.IHttpDns;
import com.sumian.common.h5.WebViewManger;
import com.sumian.common.helper.ToastHelper;
import com.sumian.common.operator.AppOperator;
import com.sumian.common.social.OpenEngine;
import com.sumian.common.social.analytics.OpenAnalytics;
import com.sumian.common.social.login.OpenLogin;
import com.sumian.hw.device.model.DeviceModel;
import com.sumian.hw.gather.FileHelper;
import com.sumian.hw.job.JobScheduler;
import com.sumian.hw.leancloud.HwLeanCloudHelper;
import com.sumian.hw.leancloud.player.VoicePlayer;
import com.sumian.hw.report.viewModel.ReportModel;
import com.sumian.hw.upgrade.model.VersionModel;
import com.sumian.sd.BuildConfig;
import com.sumian.sd.account.model.AccountViewModel;
import com.sumian.sd.doctor.model.DoctorViewModel;
import com.sumian.sd.leancloud.LeanCloudManager;
import com.sumian.sd.network.NetworkManager;
import com.sumian.sd.network.api.HwApi;
import com.sumian.sd.network.api.SdApi;
import com.sumian.sd.service.advisory.model.AdvisoryViewModel;
import com.sumian.sd.theme.three.SkinConfig;
import com.sumian.sd.theme.three.attr.CardViewAttr;
import com.sumian.sd.theme.three.attr.ColorfulProgressRingBgAttr;
import com.sumian.sd.theme.three.attr.CountSleepDurationTextViewAttr;
import com.sumian.sd.theme.three.attr.SleepAvgAndCompareAttr;
import com.sumian.sd.theme.three.attr.SwipeRefreshLayoutAttr;
import com.sumian.sd.theme.three.attr.SwipeRefreshLayoutBgAttr;
import com.sumian.sd.theme.three.attr.TouchDailySleepHistogramViewCoordinateAttr;
import com.sumian.sd.theme.three.attr.TouchDailySleepHistogramViewDeepAttr;
import com.sumian.sd.theme.three.attr.TouchDailySleepHistogramViewEmptyLableTextAttr;
import com.sumian.sd.theme.three.attr.TouchDailySleepHistogramViewEogAttr;
import com.sumian.sd.theme.three.attr.TouchDailySleepHistogramViewLightAttr;
import com.sumian.sd.theme.three.attr.TouchDailySleepHistogramViewSoberAttr;
import com.sumian.sd.theme.three.attr.TouchDailySleepHistogramViewTextAttr;
import com.sumian.sd.theme.three.attr.WeekSleepHistogramViewCoordinateAttr;
import com.sumian.sd.theme.three.attr.WeekSleepHistogramViewDeepAttr;
import com.sumian.sd.theme.three.attr.WeekSleepHistogramViewEmptyTextAttr;
import com.sumian.sd.theme.three.attr.WeekSleepHistogramViewEogAttr;
import com.sumian.sd.theme.three.attr.WeekSleepHistogramViewLableTextAttr;
import com.sumian.sd.theme.three.attr.WeekSleepHistogramViewLightTextAttr;
import com.sumian.sd.theme.three.attr.WeekSleepHistogramViewSoberTextAttr;
import com.sumian.sd.theme.three.loader.SkinManager;

/**
 * Created by jzz
 * on 2018/1/15.
 * desc:
 */

public final class AppManager {

    private AccountViewModel mAccountViewModel;
    private AdvisoryViewModel mAdvisoryViewModel;
    private DoctorViewModel mDoctorViewModel;
    private OpenEngine mOpenEngine;

    private volatile VersionModel mVersionModel;
    private volatile DeviceModel mDeviceModel;
    private volatile ReportModel mReportModel;

    private volatile NetworkManager mNetworkManager;

    private JobScheduler mJobScheduler;
    private VoicePlayer mVoicePlayer;

    private SdApi mSdApi;
    private HwApi mHwApi;

    private IHttpDns mHttpDns;

    private AppManager() {
    }

    public static AppManager getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static volatile AppManager INSTANCE = new AppManager();
    }

    public static synchronized AccountViewModel getAccountViewModel() {
        return Holder.INSTANCE.mAccountViewModel;
    }

    public static synchronized AdvisoryViewModel getAdvisoryViewModel() {
        return Holder.INSTANCE.mAdvisoryViewModel == null ? Holder.INSTANCE.mAdvisoryViewModel = new AdvisoryViewModel() : Holder.INSTANCE.mAdvisoryViewModel;
    }

    public static synchronized DoctorViewModel getDoctorViewModel() {
        return Holder.INSTANCE.mDoctorViewModel == null ? Holder.INSTANCE.mDoctorViewModel = new DoctorViewModel() : Holder.INSTANCE.mDoctorViewModel;
    }

    public static synchronized SdApi getSdHttpService() {
        return Holder.INSTANCE.mSdApi == null ? Holder.INSTANCE.mSdApi = Holder.INSTANCE.mNetworkManager.installSdHttpRequest() : Holder.INSTANCE.mSdApi;
    }

    public static synchronized HwApi getHwHttpService() {
        return Holder.INSTANCE.mHwApi == null ? Holder.INSTANCE.mHwApi = Holder.INSTANCE.mNetworkManager.installHwHttpRequest() : Holder.INSTANCE.mHwApi;
    }

    public static synchronized OpenLogin getOpenLogin() {
        return Holder.INSTANCE.mOpenEngine.getOpenLogin();
    }

    public static synchronized OpenAnalytics getOpenAnalytics() {
        return Holder.INSTANCE.mOpenEngine.getOpenAnalytics();
    }

    public static synchronized IHttpDns getHttpDns() {
        return Holder.INSTANCE.mHttpDns;
    }

    public static synchronized VersionModel getVersionModel() {
        return Holder.INSTANCE.mVersionModel == null ? Holder.INSTANCE.mVersionModel = new VersionModel() : Holder.INSTANCE
                .mVersionModel;
    }

    public static synchronized DeviceModel getDeviceModel() {
        return Holder.INSTANCE.mDeviceModel == null ? Holder.INSTANCE.mDeviceModel = new DeviceModel() : Holder.INSTANCE
                .mDeviceModel;
    }

    public static synchronized ReportModel getReportModel() {
        return Holder.INSTANCE.mReportModel == null ? Holder.INSTANCE.mReportModel = new ReportModel() : Holder.INSTANCE.mReportModel;
    }

    public static synchronized JobScheduler getJobScheduler() {
        return Holder.INSTANCE.mJobScheduler == null ? Holder.INSTANCE.mJobScheduler = new JobScheduler(App.Companion.getAppContext()) : Holder.INSTANCE
                .mJobScheduler;
    }

    public static synchronized VoicePlayer getVoicePlayer() {
        return Holder.INSTANCE.mVoicePlayer == null ? Holder.INSTANCE.mVoicePlayer = new VoicePlayer() : Holder.INSTANCE.mVoicePlayer;
    }

    public static synchronized BlueManager getBlueManager() {
        return BlueManager.init();
    }

    public void init(@NonNull Context context) {
        initNetWork(context);
        initUtils(context);
        initEmojiCompat(context);
        initAccountViewModel((Application) context);
        initLeanCloud(context);
        initOpenEngine(context);
        initBlueManager(context);
        HwLeanCloudHelper.init(context);
        initKefu(context);
        initWebView();
        initSkin(context);
    }

    private void initNetWork(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            //注册 aliyun httpDns
            this.mHttpDns = new HttpDnsEngine().init(context, BuildConfig.DEBUG, BuildConfig.HTTP_DNS_ACCOUNT_ID, BuildConfig.HTTP_DNS_SECRET_KEY);
            this.mHttpDns.setPreHostsList(BuildConfig.BASE_URL, BuildConfig.HW_BASE_URL, BuildConfig.BASE_H5_URL);
        }
        //注册网络引擎框架
        this.mNetworkManager = NetworkManager.create();
    }

    private void initLeanCloud(Context context) {
        LeanCloudManager.init(context);
    }

    private void initEmojiCompat(Context context) {
        EmojiCompat.init(new BundledEmojiCompatConfig(context));
    }

    private void initOpenEngine(Context context) {
        if (mOpenEngine == null) {
            OpenEngine.init(context, BuildConfig.DEBUG, BuildConfig.UMENG_APP_KEY, BuildConfig.UMENG_CHANNEL, BuildConfig.UMENG_PUSH_SECRET);
            mOpenEngine = new OpenEngine().create(context, BuildConfig.DEBUG, BuildConfig.WECHAT_APP_ID, BuildConfig.WECHAT_APP_SECRET);
        }
    }

    private void initAccountViewModel(Application context) {
        if (mAccountViewModel == null) {
            mAccountViewModel = new AccountViewModel(context);
            mAccountViewModel.loadTokenFromSp();
        }
    }

    private void initUtils(Context context) {
        ToastHelper.init(context);
        Utils.init(context);
        ToastUtils.setGravity(Gravity.CENTER, 0, 0);
    }

    private void initSkin(@NonNull Context context) {
        //第三方皮肤插件初始化
        SkinManager.getInstance().init(context);
        SkinConfig.setCanChangeStatusColor(false);
        SkinConfig.setCanChangeFont(false);
        if (BuildConfig.DEBUG) {
            SkinConfig.setDebug(true);
        }
        SkinConfig.addSupportAttr("csdtv_default_drawable", new CountSleepDurationTextViewAttr());
        SkinConfig.addSupportAttr("cardBackgroundColor", new CardViewAttr());
        SkinConfig.addSupportAttr("brv_progress_color", new SwipeRefreshLayoutAttr());
        SkinConfig.addSupportAttr("brv_progress_bg_color", new SwipeRefreshLayoutBgAttr());

        SkinConfig.addSupportAttr("tdshv_coordinate_color", new TouchDailySleepHistogramViewCoordinateAttr());
        SkinConfig.addSupportAttr("tdshv_deep_color", new TouchDailySleepHistogramViewDeepAttr());
        SkinConfig.addSupportAttr("tdshv_empty_text_color", new TouchDailySleepHistogramViewEmptyLableTextAttr());
        SkinConfig.addSupportAttr("tdshv_eog_color", new TouchDailySleepHistogramViewEogAttr());
        SkinConfig.addSupportAttr("tdshv_light_color", new TouchDailySleepHistogramViewLightAttr());
        SkinConfig.addSupportAttr("tdshv_sober_color", new TouchDailySleepHistogramViewSoberAttr());
        SkinConfig.addSupportAttr("tdshv_label_text_color", new TouchDailySleepHistogramViewTextAttr());

        SkinConfig.addSupportAttr("coordinate_color", new WeekSleepHistogramViewCoordinateAttr());
        SkinConfig.addSupportAttr("deep_color", new WeekSleepHistogramViewDeepAttr());
        SkinConfig.addSupportAttr("eog_color", new WeekSleepHistogramViewEogAttr());
        SkinConfig.addSupportAttr("light_color", new WeekSleepHistogramViewLightTextAttr());
        SkinConfig.addSupportAttr("sober_color", new WeekSleepHistogramViewSoberTextAttr());
        SkinConfig.addSupportAttr("label_text_color", new WeekSleepHistogramViewLableTextAttr());
        SkinConfig.addSupportAttr("empty_text_color", new WeekSleepHistogramViewEmptyTextAttr());

        SkinConfig.addSupportAttr("label_icon", new SleepAvgAndCompareAttr());

        SkinConfig.addSupportAttr("cpv_ring_bg_color", new ColorfulProgressRingBgAttr());
        //SkinConfig.addSupportAttr("label_icon", new SleepAvgAndCompareAttr());

        // SkinConfig.enableGlobalSkinApply();
    }

    private void initWebView() {
        WebViewManger webViewManger = WebViewManger.getInstance();
        webViewManger.setBaseUrl(BuildConfig.BASE_H5_URL);
        if (this.mHttpDns != null) {
            webViewManger.registerHttpDnsEngine(this.mHttpDns);
        }
        webViewManger.setDebug(BuildConfig.DEBUG);
        AppManager.getAccountViewModel().getLiveDataToken().observeForever(token -> webViewManger.setToken(token == null ? null : token.token));
    }

    private void initKefu(Context context) {
        // Kefu SDK 初始化
        ChatClient.Options options = new ChatClient.Options();
        options.setConsoleLog(BuildConfig.DEBUG);
        options.setAppkey(BuildConfig.EASEMOB_APP_KEY);//必填项，appkey获取地址：kefu.easemob.com，“管理员模式 > 渠道管理 > 手机APP”页面的关联的“AppKey”
        options.setTenantId(BuildConfig.EASEMOB_TENANT_ID);//必填项，tenantId获取地址：kefu.easemob.com，“管理员模式 > 设置 > 企业信息”页面的“租户ID”
        if (!ChatClient.getInstance().init(context, options)) {
            return;
        }
        // Kefu EaseUI的初始化
        UIProvider.getInstance().init(context);
    }

    private void initBlueManager(Context context) {
        AppOperator.runOnThread(() -> {
            FileHelper.init();
            boolean externalStorageWritable = FileHelper.init().isExternalStorageWritable();
            if (externalStorageWritable) {
                FileHelper.createSDDir(FileHelper.FILE_DIR);
            }
            BlueManager.init().with(context);
        });
    }
}
