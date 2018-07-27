package com.sumian.app.improve.main;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.hyphenate.helpdesk.easeui.UIProvider;
import com.sumian.app.R;
import com.sumian.app.app.App;
import com.sumian.app.app.AppManager;
import com.sumian.app.base.BaseActivity;
import com.sumian.app.base.BasePagerFragment;
import com.sumian.app.common.util.NumberUtil;
import com.sumian.app.common.util.UiUtil;
import com.sumian.app.improve.consultant.ConsultantFragment;
import com.sumian.app.improve.device.fragment.DeviceFragment;
import com.sumian.app.improve.fragment.MeFragment;
import com.sumian.app.improve.main.bean.PushReport;
import com.sumian.app.improve.report.ReportFragment;
import com.sumian.app.leancloud.LeanCloudHelper;
import com.sumian.app.network.callback.BaseResponseCallback;
import com.sumian.app.network.response.AppUpgradeInfo;
import com.sumian.app.push.ReportPushManager;
import com.sumian.app.setting.dialog.UpgradeDialog;
import com.sumian.app.upgrade.model.VersionModel;
import com.sumian.app.upgrade.presenter.VersionPresenter;
import com.sumian.app.widget.nav.NavTab;
import com.sumian.app.widget.nav.TabButton;
import com.sumian.blue.model.BluePeripheral;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import retrofit2.Call;

public class HomeActivity extends BaseActivity implements NavTab.OnTabChangeListener,
    LeanCloudHelper.OnShowMsgDotCallback, VersionModel.ShowDotCallback {

    private static final String TAG = HomeActivity.class.getSimpleName();

    @BindView(R.id.main_container)
    FrameLayout mMainContainer;
    @BindView(R.id.tab_device)
    TabButton mTabDevice;
    @BindView(R.id.tab_report)
    TabButton mTabReport;
    @BindView(R.id.tab_consultant)
    TabButton mTabConsultant;
    @BindView(R.id.tab_me)
    TabButton mTabMe;
    @BindView(R.id.tab_main)
    NavTab mTabMain;

    private BasePagerFragment[] mPagerFragments;
    private int mCurrentPosition = -1;
    private static final String KEY_PUSH_REPORT_SCHEME = "key_push_report_scheme";

    public static void show(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        if (context instanceof Application || context instanceof Service) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        context.startActivity(intent);
    }

    public static Intent getLaunchIntentForPushReport(Context context, String scheme) {
        Intent intent = new Intent(context, HomeActivity.class);
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
        mTabMain.setOnTabChangeListener(this);
        //注册站内信消息接收容器
        LeanCloudHelper.addOnAdminMsgCallback(this);
        AppManager.getVersionModel().registerShowDotCallback(this);
        if (mPagerFragments == null) {
            mPagerFragments = new BasePagerFragment[]{DeviceFragment.newInstance(),
                ReportFragment.newInstance(), ConsultantFragment.newInstance(), MeFragment.newInstance()};
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
        if (AppManager.getAccountModel().isLogin()) {
            LeanCloudHelper.loginLeanCloud();
            LeanCloudHelper.registerPushService();
            Call<Object> call = AppManager.getNetEngine().getHttpService().sendHeartbeats("open_app");
            call.enqueue(new BaseResponseCallback<Object>() {
                @Override
                protected void onSuccess(Object response) {

                }

                @Override
                protected void onFailure(String error) {

                }
            });
        }

        AppManager.getJobScheduler().checkJobScheduler();

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

        AppManager.getNetEngine().getHttpService().syncUpgradeAppInfo(map).enqueue(new BaseResponseCallback<AppUpgradeInfo>() {
            @Override
            protected void onSuccess(AppUpgradeInfo response) {
                PackageInfo packageInfo = UiUtil.getPackageInfo(App.getAppContext());

                AppUpgradeInfo appUpgradeInfo = response;
                if (appUpgradeInfo == null) {//相同版本或没有新版本
                    appUpgradeInfo = new AppUpgradeInfo();
                    appUpgradeInfo.version = packageInfo.versionName;
                    AppManager.getVersionModel().notifyAppDot(false);
                } else {
                    AppManager.getVersionModel().notifyAppDot(NumberUtil.formatVersionCode(response.version) > packageInfo.versionCode);
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

                AppManager.getVersionModel().setAppUpgradeInfo(appUpgradeInfo);
            }

            @Override
            protected void onFailure(String error) {

            }

            @Override
            protected void onNotFound(String error) {
                super.onNotFound(error);
                AppUpgradeInfo appUpgradeInfo = new AppUpgradeInfo();
                appUpgradeInfo.version = packageInfo.versionName;

                AppManager.getVersionModel().notifyAppDot(false);
            }
        });
    }

    @Override
    public void tab(TabButton tabButton, int position) {
//        if (mCurrentPosition == position) return;
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
        mCurrentPosition = position;
    }

    @Override
    public void onBackPressed() {
        UiUtil.exitApp(this);
    }

    @Override
    protected void onRelease() {
        super.onRelease();
        BluePeripheral bluePeripheral = AppManager.getBlueManager().getBluePeripheral();
        if (bluePeripheral != null) {
            bluePeripheral.close();
        }
        LeanCloudHelper.removeOnAdminMsgCallback(this);
        AppManager.getVersionModel().unRegisterShowDotCallback(this);
        AppManager.getBlueManager().release();
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
