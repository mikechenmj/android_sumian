package com.sumian.hw.improve.main;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.blankj.utilcode.util.ActivityUtils;
import com.hyphenate.helpdesk.easeui.UIProvider;
import com.sumian.blue.model.BluePeripheral;
import com.sumian.hw.base.HwBaseActivity;
import com.sumian.hw.base.HwBasePagerFragment;
import com.sumian.hw.improve.consultant.ConsultantFragment;
import com.sumian.hw.improve.device.fragment.DeviceFragment;
import com.sumian.hw.improve.fragment.HwMeFragment;
import com.sumian.hw.improve.main.bean.PushReport;
import com.sumian.hw.improve.report.ReportFragment;
import com.sumian.hw.leancloud.HwLeanCloudHelper;
import com.sumian.hw.network.callback.BaseResponseCallback;
import com.sumian.hw.push.ReportPushManager;
import com.sumian.hw.upgrade.model.VersionModel;
import com.sumian.hw.utils.AppUtil;
import com.sumian.hw.widget.nav.NavTab;
import com.sumian.hw.widget.nav.TabButton;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.UserInfo;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.main.MainActivity;

import retrofit2.Call;

public class HwMainActivity extends HwBaseActivity implements NavTab.OnTabChangeListener,
        HwLeanCloudHelper.OnShowMsgDotCallback, VersionModel.ShowDotCallback {

    FrameLayout mMainContainer;
    TabButton mTabDevice;
    TabButton mTabReport;
    TabButton mTabConsultant;
    TabButton mTabMe;
    NavTab mTabMain;

    private String[] mFragmentTags = {"tab_0", "tab_1", "tab_2", "tab_3"};

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
        HwLeanCloudHelper.addOnAdminMsgCallback(this);
        AppManager.getVersionModel().registerShowDotCallback(this);
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
            HwLeanCloudHelper.loginLeanCloud();
            HwLeanCloudHelper.registerPushService();
        }

        AppManager.getJobScheduler().checkJobScheduler();

        UIProvider.getInstance().showDotCallback(msgLength -> {
            HwLeanCloudHelper.haveCustomerMsg(msgLength);
            onShowMsgDotCallback(0, 0, msgLength);
        });

        runUiThread(() -> {
            syncUserInfo();
            sendHeartBeats();
        }, 200);

        runUiThread(() -> HwLeanCloudHelper.haveCustomerMsg(UIProvider.getInstance().isHaveMsgSize()), 500);
    }

    private void sendHeartBeats() {
        Call<Object> call = AppManager.getHwNetEngine().getHttpService().sendHeartbeats("open_app");
        call.enqueue(new BaseResponseCallback<Object>() {
            @Override
            protected void onSuccess(Object response) {

            }

            @Override
            protected void onFailure(int code, String message) {

            }
        });
    }

    private void syncUserInfo() {
        Call<UserInfo> call = AppManager.getHwNetEngine().getHttpService().getUserInfo("doctor");
        call.enqueue(new BaseResponseCallback<UserInfo>() {
            @Override
            protected void onSuccess(UserInfo response) {
                AppManager.getAccountViewModel().updateUserInfo(response);
            }

            @Override
            protected void onFailure(int code, String message) {

            }

        });
    }

    @Override
    public void tab(TabButton tabButton, int position) {
        if (position == 2) {
            mTabMain.onClick(mTabDevice);
            launchAnotherMainActivity();
            return;
        }
        HwBasePagerFragment fragmentByTag;
        String tag;
        for (int i = 0, len = mFragmentTags.length; i < len; i++) {
            tag = mFragmentTags[i];
            fragmentByTag = (HwBasePagerFragment) getSupportFragmentManager().findFragmentByTag(tag);
            if (fragmentByTag == null) {
                fragmentByTag = createFragmentByPosition(position);
            }
            if (position == i) {
                if (fragmentByTag.isAdded()) {
                    getSupportFragmentManager().beginTransaction().show(fragmentByTag).runOnCommit(fragmentByTag::onEnterTab).commit();
                } else {
                    getSupportFragmentManager().beginTransaction().add(R.id.main_container, fragmentByTag, tag).runOnCommit(fragmentByTag::onEnterTab).commit();
                }
            } else {
                getSupportFragmentManager().beginTransaction().hide(fragmentByTag).commit();
            }
        }
    }

    private void launchAnotherMainActivity() {
        ActivityUtils.startActivity(MainActivity.class);
    }

    @Override
    public void onBackPressed() {
        AppUtil.exitApp();
    }

    @Override
    protected void onRelease() {
        super.onRelease();
        BluePeripheral bluePeripheral = AppManager.getBlueManager().getBluePeripheral();
        if (bluePeripheral != null) {
            bluePeripheral.close();
        }
        HwLeanCloudHelper.removeOnAdminMsgCallback(this);
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
            // TODO 注释掉该功能,目前消息中心小红点变化转需求,移入下一个新迭代的新消息中心
            // this.mTabConsultant.showDot(doctorMsgLen + customerMsgLen > 0 ? android.view.View.VISIBLE : android.view.View.GONE);
            this.mTabMe.showDot(adminMsgLen > 0 ? android.view.View.VISIBLE : android.view.View.GONE);
        });
    }

    @Override
    public void showDot(boolean isShowAppDot, boolean isShowMonitorDot, boolean isShowSleepyDot) {
        // Log.e(TAG, "showDot: ------->" + isShowAppDot);
        runUiThread(() -> this.mTabMe.showDot(isShowAppDot || isShowMonitorDot || isShowSleepyDot ? android.view.View.VISIBLE : android.view.View.GONE));
    }

    private HwBasePagerFragment createFragmentByPosition(int position) {
        switch (position) {
            case 0:
                return DeviceFragment.newInstance();
            case 1:
                return ReportFragment.newInstance();
            case 2:
                return ConsultantFragment.newInstance();
            case 3:
                return HwMeFragment.newInstance();
            default:
                throw new RuntimeException("Illegal tab position");
        }
    }
}
