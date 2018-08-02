package com.sumian.hw.improve.main;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.blankj.utilcode.util.ActivityUtils;
import com.hyphenate.helpdesk.easeui.UIProvider;
import com.sumian.blue.model.BluePeripheral;
import com.sumian.sleepdoctor.account.model.AccountViewModel;
import com.sumian.hw.app.App;
import com.sumian.hw.app.HwAppManager;
import com.sumian.hw.base.BaseActivity;
import com.sumian.hw.base.BasePagerFragment;
import com.sumian.hw.common.util.NumberUtil;
import com.sumian.hw.common.util.UiUtil;
import com.sumian.hw.improve.consultant.ConsultantFragment;
import com.sumian.hw.improve.device.fragment.DeviceFragment;
import com.sumian.hw.improve.fragment.HwMeFragment;
import com.sumian.hw.improve.main.bean.PushReport;
import com.sumian.hw.improve.report.ReportFragment;
import com.sumian.hw.leancloud.LeanCloudHelper;
import com.sumian.hw.network.callback.BaseResponseCallback;
import com.sumian.hw.network.response.AppUpgradeInfo;
import com.sumian.hw.push.ReportPushManager;
import com.sumian.hw.setting.dialog.UpgradeDialog;
import com.sumian.hw.upgrade.model.VersionModel;
import com.sumian.hw.upgrade.presenter.VersionPresenter;
import com.sumian.hw.utils.JsonUtil;
import com.sumian.hw.widget.nav.NavTab;
import com.sumian.hw.widget.nav.TabButton;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.Token;
import com.sumian.sleepdoctor.account.bean.UserInfo;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.main.MainActivity;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

public class HwMainActivity extends BaseActivity implements NavTab.OnTabChangeListener,
        LeanCloudHelper.OnShowMsgDotCallback, VersionModel.ShowDotCallback {

    private static final String TAG = HwMainActivity.class.getSimpleName();

    FrameLayout mMainContainer;
    TabButton mTabDevice;
    TabButton mTabReport;
    TabButton mTabConsultant;
    TabButton mTabMe;
    NavTab mTabMain;

    private BasePagerFragment[] mPagerFragments;
    private static final String KEY_PUSH_REPORT_SCHEME = "key_push_report_scheme";

    public static void show(Context context) {
        Intent intent = new Intent(context, HwMainActivity.class);
        if (context instanceof Application || context instanceof Service) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        context.startActivity(intent);
    }

    public static Intent getLaunchIntentForPushReport(Context context, String scheme) {
        Intent intent = new Intent(context, HwMainActivity.class);
        intent.putExtra(KEY_PUSH_REPORT_SCHEME, scheme);
        return intent;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.hw_activity_main;
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        if (bundle == null) {
            return true;
        }
        String scheme = bundle.getString(KEY_PUSH_REPORT_SCHEME);
        if (scheme == null) {
            return true;
        }
        ReportPushManager.getInstance().setPushReportByUriStr(scheme);
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        PushReport pushReport = ReportPushManager.getInstance().getPushReport();
        if (pushReport != null) {
            mTabMain.onClick(mTabReport);
        }
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mMainContainer = findViewById(R.id.main_container);
        mTabDevice = findViewById(R.id.tab_device);
        mTabReport = findViewById(R.id.tab_report);
        mTabConsultant = findViewById(R.id.tab_consultant);
        mTabMe = findViewById(R.id.tab_me);
        mTabMain = findViewById(R.id.tab_main);

        mTabMain.setOnTabChangeListener(this);
        //注册站内信消息接收容器
        LeanCloudHelper.addOnAdminMsgCallback(this);
        HwAppManager.getVersionModel().registerShowDotCallback(this);
        if (mPagerFragments == null) {
            mPagerFragments = new BasePagerFragment[]{DeviceFragment.newInstance(),
                    ReportFragment.newInstance(), ConsultantFragment.newInstance(), HwMeFragment.newInstance()};
        }

        PushReport pushReport = ReportPushManager.getInstance().getPushReport();
        if (pushReport != null) {
            mTabMain.onClick(mTabReport);
        } else {
            mTabMain.onClick(mTabDevice);
        }
    }

    @Override
    protected void initData() {
        super.initData();
        if (AppManager.getAccountViewModel().isLogin()) {
            LeanCloudHelper.loginLeanCloud();
            LeanCloudHelper.registerPushService();
            Call<Object> call = HwAppManager.getNetEngine().getHttpService().sendHeartbeats("open_app");
            call.enqueue(new BaseResponseCallback<Object>() {
                @Override
                protected void onSuccess(Object response) {

                }

                @Override
                protected void onFailure(String error) {

                }
            });
        }

        HwAppManager.getJobScheduler().checkJobScheduler();

        VersionPresenter.init().syncAppVersionInfo();

        UIProvider.getInstance().showDotCallback(msgLength -> {
            LeanCloudHelper.haveCustomerMsg(msgLength);
            onShowMsgDotCallback(0, 0, msgLength);
        });

        checkAppVersion();
        runUiThread(() -> LeanCloudHelper.haveCustomerMsg(UIProvider.getInstance().isHaveMsgSize()), 500);
    }

    private void checkAppVersion() {
        Map<String, String> map = new HashMap<>();

        PackageInfo packageInfo = UiUtil.getPackageInfo(App.getAppContext());

        map.put("type", "1");
        map.put("current_version", packageInfo.versionName);

        HwAppManager.getNetEngine().getHttpService().syncUpgradeAppInfo(map).enqueue(new BaseResponseCallback<AppUpgradeInfo>() {
            @Override
            protected void onSuccess(AppUpgradeInfo response) {
                PackageInfo packageInfo = UiUtil.getPackageInfo(App.getAppContext());

                AppUpgradeInfo appUpgradeInfo = response;
                if (appUpgradeInfo == null) {//相同版本或没有新版本
                    appUpgradeInfo = new AppUpgradeInfo();
                    appUpgradeInfo.version = packageInfo.versionName;
                    HwAppManager.getVersionModel().notifyAppDot(false);
                } else {
                    HwAppManager.getVersionModel().notifyAppDot(NumberUtil.formatVersionCode(response.version) > packageInfo.versionCode);
                    if (NumberUtil.formatVersionCode(response.version) > packageInfo.versionCode) {
                        UpgradeDialog upgradeDialog = new UpgradeDialog();
                        Bundle args = new Bundle();
                        args.putSerializable("app_info", appUpgradeInfo);
                        upgradeDialog.setArguments(args);
                        upgradeDialog.show(getSupportFragmentManager(), UpgradeDialog.class.getSimpleName());
                    }
                }

                try {
                    AppUpgradeInfo copyAppUpgradeInfo = appUpgradeInfo.clone();
                    copyAppUpgradeInfo.version = packageInfo.versionName;

                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }

                HwAppManager.getVersionModel().setAppUpgradeInfo(appUpgradeInfo);
            }

            @Override
            protected void onFailure(String error) {

            }

            @Override
            protected void onNotFound(String error) {
                super.onNotFound(error);
                AppUpgradeInfo appUpgradeInfo = new AppUpgradeInfo();
                appUpgradeInfo.version = packageInfo.versionName;

                HwAppManager.getVersionModel().notifyAppDot(false);
            }
        });
    }

    @Override
    public void tab(TabButton tabButton, int position) {
        if (position == 2) {
            launchSleepDoctor();
            finish();
            return;
        }
        BasePagerFragment pagerFragment;
        BasePagerFragment fragmentByTag;
        String tag;
        for (int i = 0, len = mPagerFragments.length; i < len; i++) {
            pagerFragment = mPagerFragments[i];
            tag = pagerFragment.getClass().getSimpleName();
            fragmentByTag = (BasePagerFragment) getSupportFragmentManager().findFragmentByTag(tag);
            if (position == i) {
                if (fragmentByTag != null && fragmentByTag.isAdded()) {
                    getSupportFragmentManager().beginTransaction().show(pagerFragment).runOnCommit(pagerFragment::onEnterTab).commit();
                } else {
                    getSupportFragmentManager().beginTransaction().add(R.id.main_container, pagerFragment, tag).runOnCommit(pagerFragment::onEnterTab).commit();
                }
            } else {
                if (fragmentByTag != null) {
                    getSupportFragmentManager().beginTransaction().hide(pagerFragment).commit();
                }
            }
        }
    }

    private void launchSleepDoctor() {
        ActivityUtils.startActivity(MainActivity.class);
    }

    @Override
    public void onBackPressed() {
        UiUtil.exitApp(this);
    }

    @Override
    protected void onRelease() {
        super.onRelease();
        BluePeripheral bluePeripheral = HwAppManager.getBlueManager().getBluePeripheral();
        if (bluePeripheral != null) {
            bluePeripheral.close();
        }
        LeanCloudHelper.removeOnAdminMsgCallback(this);
        HwAppManager.getVersionModel().unRegisterShowDotCallback(this);
        HwAppManager.getBlueManager().release();
    }

    @Override
    public void onShowMsgDotCallback(int adminMsgLen, int doctorMsgLen, int customerMsgLen) {
        onHideMsgCallback(adminMsgLen, doctorMsgLen, customerMsgLen);
    }

    @Override
    public void onHideMsgCallback(int adminMsgLen, int doctorMsgLen, int customerMsgLen) {
        runUiThread(() -> {
            this.mTabConsultant.showDot(doctorMsgLen + customerMsgLen > 0 ? android.view.View.VISIBLE :
                    android.view.View.GONE);
            this.mTabMe.showDot(adminMsgLen > 0 ? android.view.View.VISIBLE : android.view.View.GONE);
        });
    }

    @Override
    public void showDot(boolean isShowAppDot, boolean isShowMonitorDot, boolean isShowSleepyDot) {
        // Log.e(TAG, "showDot: ------->" + isShowAppDot);
        runUiThread(() -> this.mTabMe.showDot(isShowAppDot || isShowMonitorDot || isShowSleepyDot ? android.view.View.VISIBLE : android.view.View.GONE));
    }
}
