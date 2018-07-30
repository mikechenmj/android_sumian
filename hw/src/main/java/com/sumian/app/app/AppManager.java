package com.sumian.app.app;

import android.content.Context;
import android.text.TextUtils;

import com.hyphenate.chat.ChatClient;
import com.hyphenate.helpdesk.easeui.UIProvider;
import com.sumian.app.BuildConfig;
import com.sumian.app.account.model.AccountModel;
import com.sumian.app.common.helper.ToastHelper;
import com.sumian.app.common.operator.AppOperator;
import com.sumian.app.common.util.StreamUtil;
import com.sumian.app.gather.FileHelper;
import com.sumian.app.improve.device.model.DeviceModel;
import com.sumian.app.improve.report.viewModel.ReportModel;
import com.sumian.app.job.JobScheduler;
import com.sumian.app.leancloud.LeanCloudHelper;
import com.sumian.app.leancloud.player.VoicePlayer;
import com.sumian.app.network.api.SleepyV1Api;
import com.sumian.app.network.engine.NetEngine;
import com.sumian.app.upgrade.model.VersionModel;
import com.sumian.blue.manager.BlueManager;
import com.sumian.open.OpenEngine;
import com.sumian.open.analytics.OpenAnalytics;
import com.sumian.open.login.OpenLogin;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by jzz
 * on 2017/9/25
 * <p>
 * desc: all app business  manager
 */
@SuppressWarnings("unused")
public final class AppManager {

    // private static final String TAG = AppManager.class.getSimpleName();

    private static volatile AppManager INSTANCE = null;

    private volatile VersionModel mVersionModel;
    private volatile DeviceModel mDeviceModel;
    private volatile AccountModel mAccountModel;
    private volatile ReportModel mReportModel;

    private NetEngine mNetEngine;
    private JobScheduler mJobScheduler;
    private OpenEngine mOpenEngine;
    private VoicePlayer mVoicePlayer;

    private AppManager(Context context) {
        init(context);
    }

    static void create(Context context) {
        if (INSTANCE == null) {
            synchronized (AppManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppManager(context);
                }
            }
        }
    }

    public static synchronized NetEngine getNetEngine() {
        return INSTANCE.mNetEngine == null ? INSTANCE.mNetEngine = new NetEngine() : INSTANCE.mNetEngine;
    }

    public static synchronized SleepyV1Api getV1HttpService() {
        return getNetEngine().getV1HttpService();
    }

    public static synchronized OpenLogin getOpenLogin() {
        if (INSTANCE.mOpenEngine == null) {
            INSTANCE.mOpenEngine = new OpenEngine().register(App.getAppContext(), BuildConfig.DEBUG, BuildConfig.WECHAT_APP_ID, BuildConfig.WECHAT_APP_SECRET);
            return INSTANCE.mOpenEngine.getOpenLogin();
        } else {
            return INSTANCE.mOpenEngine.getOpenLogin();
        }
    }

    public static synchronized OpenAnalytics getOpenAnalytics() {
        if (INSTANCE.mOpenEngine == null) {
            INSTANCE.mOpenEngine = new OpenEngine().register(App.getAppContext(), BuildConfig.DEBUG, BuildConfig.WECHAT_APP_ID, BuildConfig.WECHAT_APP_SECRET);
            return INSTANCE.mOpenEngine.getOpenAnalytics();
        } else {
            return INSTANCE.mOpenEngine.getOpenAnalytics();
        }
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

    public static synchronized AccountModel getAccountModel() {
        return INSTANCE.mAccountModel == null ? INSTANCE.mAccountModel = new AccountModel() : INSTANCE.mAccountModel;
    }

    public static synchronized JobScheduler getJobScheduler() {
        return INSTANCE.mJobScheduler == null ? INSTANCE.mJobScheduler = new JobScheduler(App.getAppContext()) : INSTANCE
                .mJobScheduler;
    }

    public static synchronized VoicePlayer getVoicePlayer() {
        return INSTANCE.mVoicePlayer == null ? INSTANCE.mVoicePlayer = new VoicePlayer() : INSTANCE.mVoicePlayer;
    }

    public static synchronized BlueManager getBlueManager() {
        return BlueManager.init();
    }

    private void init(Context context) {
        AppOperator.runOnThread(() -> {
            FileHelper.init();
            boolean externalStorageWritable = FileHelper.init().isExternalStorageWritable();
            if (externalStorageWritable) {
                FileHelper.createSDDir(FileHelper.FILE_DIR);
            }
            BlueManager.init().with(context);
        });

        ToastHelper.init(context);
        //leancloud
        LeanCloudHelper.init(context);

        //Umeng
        new OpenEngine().register(context, BuildConfig.DEBUG, BuildConfig.UMENG_APP_KEY, BuildConfig.UMENG_PUSH_SECRET);

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


    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    @SuppressWarnings("unused")
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            StreamUtil.close(reader);
        }
        return null;
    }
}
