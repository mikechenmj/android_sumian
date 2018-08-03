package com.sumian.sleepdoctor.app;

import android.content.Context;

import com.hyphenate.chat.ChatClient;
import com.hyphenate.helpdesk.easeui.UIProvider;
import com.sumian.blue.manager.BlueManager;
import com.sumian.common.operator.AppOperator;
import com.sumian.common.social.OpenEngine;
import com.sumian.hw.common.helper.ToastHelper;
import com.sumian.hw.gather.FileHelper;
import com.sumian.hw.improve.device.model.DeviceModel;
import com.sumian.hw.improve.report.viewModel.ReportModel;
import com.sumian.hw.job.JobScheduler;
import com.sumian.hw.leancloud.LeanCloudHelper;
import com.sumian.hw.leancloud.player.VoicePlayer;
import com.sumian.hw.network.api.SleepyV1Api;
import com.sumian.hw.network.engine.HwNetEngine;
import com.sumian.hw.upgrade.model.VersionModel;
import com.sumian.sleepdoctor.BuildConfig;

/**
 * Created by jzz
 * on 2017/9/25
 * <p>
 * desc: all app business  manager
 */
@SuppressWarnings("unused")
public final class HwAppManager {

    private static volatile HwAppManager INSTANCE = null;

    private volatile VersionModel mVersionModel;
    private volatile DeviceModel mDeviceModel;
    private volatile ReportModel mReportModel;

    private HwNetEngine mNetEngine;
    private JobScheduler mJobScheduler;
    private OpenEngine mOpenEngine;
    private VoicePlayer mVoicePlayer;

    private HwAppManager(Context context) {
        init(context);
    }

    static void create(Context context) {
        if (INSTANCE == null) {
            synchronized (HwAppManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HwAppManager(context);
                }
            }
        }
    }

    public static synchronized HwNetEngine getHwNetEngine() {
        return INSTANCE.mNetEngine == null ? INSTANCE.mNetEngine = new HwNetEngine() : INSTANCE.mNetEngine;
    }

    public static synchronized SleepyV1Api getHwV1HttpService() {
        return getHwNetEngine().getV1HttpService();
    }

    public static synchronized VersionModel getVersionModel() {
        return INSTANCE.mVersionModel == null ? INSTANCE.mVersionModel = new VersionModel() : INSTANCE
                .mVersionModel;
    }

    public static synchronized DeviceModel getDeviceModel() {
        return INSTANCE.mDeviceModel == null ? INSTANCE.mDeviceModel = new DeviceModel() : INSTANCE
                .mDeviceModel;
    }

    public static synchronized ReportModel getReportModel() {
        return INSTANCE.mReportModel == null ? INSTANCE.mReportModel = new ReportModel() : INSTANCE.mReportModel;
    }

    public static synchronized JobScheduler getJobScheduler() {
        return INSTANCE.mJobScheduler == null ? INSTANCE.mJobScheduler = new JobScheduler(App.Companion.getAppContext()) : INSTANCE
                .mJobScheduler;
    }

    public static synchronized VoicePlayer getVoicePlayer() {
        return INSTANCE.mVoicePlayer == null ? INSTANCE.mVoicePlayer = new VoicePlayer() : INSTANCE.mVoicePlayer;
    }

    public static synchronized BlueManager getBlueManager() {
        return BlueManager.init();
    }

    private void init(Context context) {
        ToastHelper.init(context);
        initBlueManager(context);
        LeanCloudHelper.init(context);
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
