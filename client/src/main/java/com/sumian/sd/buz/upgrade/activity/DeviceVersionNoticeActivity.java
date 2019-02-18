package com.sumian.sd.buz.upgrade.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.sumian.common.base.BaseViewModelActivity;
import com.sumian.common.helper.ToastHelper;
import com.sumian.common.widget.TitleBar;
import com.sumian.common.widget.refresh.SumianSwipeRefreshLayout;
import com.sumian.sd.R;
import com.sumian.sd.app.App;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.buz.devicemanager.DeviceManager;
import com.sumian.sd.buz.devicemanager.BlueDevice;
import com.sumian.sd.buz.upgrade.bean.VersionInfo;
import com.sumian.sd.buz.upgrade.model.VersionModel;
import com.sumian.sd.buz.upgrade.presenter.DeviceVersionNoticeViewModel;
import com.sumian.sd.common.network.response.AppUpgradeInfo;
import com.sumian.sd.common.utils.UiUtil;
import com.sumian.sd.widget.VersionInfoView;

import java.util.Locale;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Created by jzz
 * on 2017/11/23.
 * <p>
 * desc:
 */

public class DeviceVersionNoticeActivity extends BaseViewModelActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
        TitleBar.OnBackClickListener, VersionModel.ShowDotCallback {

    private SumianSwipeRefreshLayout mRefresh;
    private TextView mTvAppVersionName;
    private TextView mTvMonitorVersionName;
    private TextView mTvSleepyVersionName;
    private VersionInfoView mAppVersionInfo;
    private VersionInfoView mMonitorVersionInfo;
    private VersionInfoView mSleepVersionInfo;

    private DeviceVersionNoticeViewModel mPresenter;
    private Handler mHandler = new Handler();

    public static void show(Context context) {
        context.startActivity(new Intent(context, DeviceVersionNoticeActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_activity_main_version_info;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        TitleBar mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setOnBackClickListener(this);
        mRefresh = findViewById(R.id.refresh);
        mRefresh.setOnRefreshListener(this);

        mTvAppVersionName = findViewById(R.id.tv_app_version_name);
        mTvMonitorVersionName = findViewById(R.id.tv_monitor_version_name);
        mTvSleepyVersionName = findViewById(R.id.tv_sleepy_version_name);
        mAppVersionInfo = findViewById(R.id.app_version_info);
        mMonitorVersionInfo = findViewById(R.id.monitor_version_info);
        mSleepVersionInfo = findViewById(R.id.sleepy_version_info);

        findViewById(R.id.app_version_info).setOnClickListener(this);
        findViewById(R.id.monitor_version_info).setOnClickListener(this);
        findViewById(R.id.sleepy_version_info).setOnClickListener(this);

        AppManager.getVersionModel().registerShowDotCallback(this);
        DeviceVersionNoticeViewModel.init(this);
    }

    @Override
    protected void initData() {
        super.initData();
        DeviceManager.INSTANCE.getAndCheckFirmVersion();
    }

    @Override
    protected void onRelease() {
        AppManager.getVersionModel().unRegisterShowDotCallback(this);
        super.onRelease();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.app_version_info:
                UiUtil.openAppInMarket(v.getContext());
                break;
            case R.id.monitor_version_info:
                DeviceVersionUpgradeActivity.show(this, DeviceVersionUpgradeActivity.VERSION_TYPE_MONITOR, AppManager.getVersionModel().isShowMonitorVersionDot());
                break;
            case R.id.sleepy_version_info:
                DeviceVersionUpgradeActivity.show(this, DeviceVersionUpgradeActivity.VERSION_TYPE_SLEEPY, AppManager.getVersionModel().isShowSleepyVersionDot());
                break;
        }
    }

    @Override
    public void onRefresh() {
        mRefresh.setRefreshing(true);
        mPresenter.syncMonitorVersionInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        onRefresh();
    }

    @Override
    public void onBack(View v) {
        finish();
    }

    public void setPresenter(DeviceVersionNoticeViewModel presenter) {
        this.mPresenter = presenter;
    }

    public void onSyncMonitorCallback(VersionInfo versionInfo) {
        setText(mTvMonitorVersionName, String.format(Locale.getDefault(), getString(R.string.version_name_hint),
                getString(R.string.monitor), versionInfo.getVersion()));
    }

    public void onSyncSleepyCallback(VersionInfo versionInfo) {
        setText(mTvSleepyVersionName, String.format(Locale.getDefault(), getString(R.string.version_name_hint),
                getString(R.string.speed_sleeper), versionInfo.getVersion()));
    }

    public void onSyncAppVersionCallback(AppUpgradeInfo appUpgradeInfo) {
        setText(mTvAppVersionName, String.format(Locale.getDefault(), getString(R.string.version_name_hint),
                "APP", appUpgradeInfo.version));
    }

    @Override
    public void showDot(boolean isShowAppDot, boolean isShowMonitorDot, boolean isShowSleepyDot) {
        mHandler.post(() -> {
            boolean isDeviceConnected = DeviceManager.INSTANCE.isMonitorConnected();
            if (!isDeviceConnected) {
                setText(mTvMonitorVersionName, String.format(Locale.getDefault(), getString(R.string.version_name_hint),
                        getString(R.string.monitor), App.Companion.getAppContext().getString(R.string.none_connected_state_hint)));
                setText(mTvSleepyVersionName, String.format(Locale.getDefault(), getString(R.string.version_name_hint),
                        getString(R.string.speed_sleeper), App.Companion.getAppContext().getString(R.string.none_connected_state_hint)));
            }
            mAppVersionInfo.updateUpgradeInfo(isShowAppDot, null);
            mMonitorVersionInfo.updateUpgradeInfo(isShowMonitorDot, DeviceManager.INSTANCE.getMonitorSn());
            mSleepVersionInfo.updateUpgradeInfo(isShowSleepyDot, DeviceManager.INSTANCE.getSleeperSn());

            if (isDeviceConnected) {
                mMonitorVersionInfo.show();
            }
            if (isDeviceConnected && DeviceManager.INSTANCE.getSleeperStatus() == BlueDevice.STATUS_CONNECTED) {
                mSleepVersionInfo.show();
            }
        });
    }

    private void setText(TextView tv, String text) {
        mHandler.post(() -> tv.setText(text));
    }

    public void onFailure(String error) {
        ToastHelper.show(error);
    }

    public void onBegin() {
        mRefresh.setRefreshing(true);
    }

    public void onFinish() {
        mRefresh.setRefreshing(false);
    }
}
