package com.sumian.sleepdoctor.app;

import android.app.Application;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.text.emoji.EmojiCompat;
import android.support.text.emoji.bundled.BundledEmojiCompatConfig;
import android.view.Gravity;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.helpdesk.easeui.UIProvider;
import com.sumian.blue.manager.BlueManager;
import com.sumian.common.helper.ToastHelper;
import com.sumian.common.operator.AppOperator;
import com.sumian.common.social.OpenEngine;
import com.sumian.common.social.analytics.OpenAnalytics;
import com.sumian.common.social.login.OpenLogin;
import com.sumian.hw.account.activity.HwLoginActivity;
import com.sumian.hw.gather.FileHelper;
import com.sumian.hw.improve.device.model.DeviceModel;
import com.sumian.hw.improve.report.viewModel.ReportModel;
import com.sumian.hw.job.JobScheduler;
import com.sumian.hw.leancloud.HwLeanCloudHelper;
import com.sumian.hw.leancloud.player.VoicePlayer;
import com.sumian.hw.network.api.SleepyV1Api;
import com.sumian.hw.network.engine.HwNetEngine;
import com.sumian.hw.upgrade.model.VersionModel;
import com.sumian.sleepdoctor.BuildConfig;
import com.sumian.sleepdoctor.account.model.AccountViewModel;
import com.sumian.sleepdoctor.advisory.model.AdvisoryViewModel;
import com.sumian.sleepdoctor.doctor.model.DoctorViewModel;
import com.sumian.sleepdoctor.leancloud.LeanCloudManager;
import com.sumian.sleepdoctor.network.api.DoctorApi;
import com.sumian.sleepdoctor.network.engine.NetEngine;

/**
 * Created by jzz
 * on 2018/1/15.
 * desc:
 */

public final class AppManager {

    private DoctorApi mDoctorApi;
    private AccountViewModel mAccountViewModel;
    private AdvisoryViewModel mAdvisoryViewModel;
    private DoctorViewModel mDoctorViewModel;
    private OpenEngine mOpenEngine;

    private static volatile AppManager INSTANCE = null;
    private volatile VersionModel mVersionModel;
    private volatile DeviceModel mDeviceModel;
    private volatile ReportModel mReportModel;
    private HwNetEngine mHwNetEngine;
    private JobScheduler mJobScheduler;
    private VoicePlayer mVoicePlayer;


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

    public static synchronized DoctorApi getHttpService() {
        return Holder.INSTANCE.mDoctorApi == null ? Holder.INSTANCE.mDoctorApi = new NetEngine().httpRequest() : Holder.INSTANCE.mDoctorApi;
    }

    public static synchronized OpenLogin getOpenLogin() {
        return Holder.INSTANCE.mOpenEngine.getOpenLogin();
    }

    public static synchronized OpenAnalytics getOpenAnalytics() {
        return Holder.INSTANCE.mOpenEngine.getOpenAnalytics();
    }

    public static synchronized HwNetEngine getHwNetEngine() {
        return Holder.INSTANCE.mHwNetEngine == null ? Holder.INSTANCE.mHwNetEngine = new HwNetEngine() : Holder.INSTANCE.mHwNetEngine;
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
        mAccountViewModel.getLiveDataTokenInvalidState().observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean tokenIsInvalid) {
                if (tokenIsInvalid == null) {
                    return;
                }
                if (tokenIsInvalid) {
                    ActivityUtils.startActivity(HwLoginActivity.class);
                }
            }
        });
    }

    private void initUtils(Context context) {
        ToastHelper.init(context);
        Utils.init(context);
        ToastUtils.setGravity(Gravity.CENTER, 0, 0);
    }

    public static synchronized SleepyV1Api getHwV1HttpService() {
        return getHwNetEngine().getV1HttpService();
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
        initUtils(context);
        initEmojiCompat(context);
        initAccountViewModel((Application) context);
        initLeanCloud(context);
        initOpenEngine(context);
        initBlueManager(context);
        HwLeanCloudHelper.init(context);
        initKefu(context);
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
