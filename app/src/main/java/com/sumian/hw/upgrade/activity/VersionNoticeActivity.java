package com.sumian.hw.upgrade.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.TextView;

import com.sumian.blue.model.BluePeripheral;
import com.sumian.common.helper.ToastHelper;
import com.sumian.hw.base.HwBaseActivity;
import com.sumian.hw.common.util.UiUtil;
import com.sumian.hw.network.response.AppUpgradeInfo;
import com.sumian.hw.upgrade.bean.VersionInfo;
import com.sumian.hw.upgrade.contract.VersionContract;
import com.sumian.hw.upgrade.model.VersionModel;
import com.sumian.hw.upgrade.presenter.VersionPresenter;
import com.sumian.hw.widget.TitleBar;
import com.sumian.hw.widget.VersionInfoView;
import com.sumian.hw.widget.refresh.BlueRefreshView;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.app.App;
import com.sumian.sleepdoctor.app.AppManager;

import java.util.Locale;

/**
 * Created by jzz
 * on 2017/11/23.
 * <p>
 * desc:
 */

public class VersionNoticeActivity extends HwBaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
        TitleBar.OnBackListener, VersionContract.View, VersionModel.ShowDotCallback {

    TitleBar mTitleBar;
    BlueRefreshView mRefresh;
    TextView mTvAppVersionName;
    TextView mTvMonitorVersionName;
    TextView mTvSleepyVersionName;
    View mDivider;
    VersionInfoView mAppVersionInfo;
    VersionInfoView mMonitorVersionInfo;
    VersionInfoView mSleepVersionInfo;

    private VersionContract.Presenter mPresenter;

    public static void show(Context context) {
        context.startActivity(new Intent(context, VersionNoticeActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hw_activity_main_version_info;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTitleBar = findViewById(R.id.title_bar);
        mRefresh = findViewById(R.id.refresh);
        mTvAppVersionName = findViewById(R.id.tv_app_version_name);
        mTvMonitorVersionName = findViewById(R.id.tv_monitor_version_name);
        mTvSleepyVersionName = findViewById(R.id.tv_sleepy_version_name);
        mDivider = findViewById(R.id.v_divider);
        mAppVersionInfo = findViewById(R.id.app_version_info);
        mMonitorVersionInfo = findViewById(R.id.monitor_version_info);
        mSleepVersionInfo = findViewById(R.id.sleepy_version_info);

        findViewById(R.id.app_version_info).setOnClickListener(this);
        findViewById(R.id.monitor_version_info).setOnClickListener(this);
        findViewById(R.id.sleepy_version_info).setOnClickListener(this);

        mTitleBar.addOnBackListener(this);
        mRefresh.setOnRefreshListener(this);
        AppManager.getVersionModel().registerShowDotCallback(this);
        VersionPresenter.init(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mRefresh.setRefreshing(true);
        mPresenter.syncAppVersionInfo();
        mPresenter.syncMonitorVersionInfo();
    }

    @Override
    protected void onRelease() {
        AppManager.getVersionModel().unRegisterShowDotCallback(this);
        super.onRelease();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.app_version_info) {
            UiUtil.openAppInMarket(v.getContext());
        } else if (id == R.id.monitor_version_info) {
            VersionUpgradeActivity.show(this, VersionUpgradeActivity.VERSION_TYPE_MONITOR, AppManager.getVersionModel().isShowMonitorVersionDot());
        } else if (id == R.id.sleepy_version_info) {
            VersionUpgradeActivity.show(this, VersionUpgradeActivity.VERSION_TYPE_SLEEPY, AppManager.getVersionModel().isShowSleepyVersionDot());
        }
    }

    @Override
    public void onRefresh() {
        initData();
    }

    @Override
    public void onBack(View v) {
        finish();
    }

    @Override
    public void setPresenter(VersionContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onSyncMonitorCallback(VersionInfo versionInfo) {
        setText(mTvMonitorVersionName, String.format(Locale.getDefault(), getString(R.string.version_name_hint),
                getString(R.string.monitor), versionInfo.getVersion()));
    }

    @Override
    public void onSyncSleepyCallback(VersionInfo versionInfo) {
        setText(mTvSleepyVersionName, String.format(Locale.getDefault(), getString(R.string.version_name_hint),
                getString(R.string.speed_sleeper), versionInfo.getVersion()));
    }

    @Override
    public void onSyncAppVersionCallback(AppUpgradeInfo appUpgradeInfo) {
        setText(mTvAppVersionName, String.format(Locale.getDefault(), getString(R.string.version_name_hint),
                "APP", appUpgradeInfo.version));
    }

    @Override
    public void showDot(boolean isShowAppDot, boolean isShowMonitorDot, boolean isShowSleepyDot) {
        runUiThread(() -> {
            BluePeripheral bluePeripheral = AppManager.getBlueManager().getBluePeripheral();
            if (bluePeripheral == null || !bluePeripheral.isConnected()) {
                setText(mTvMonitorVersionName, String.format(Locale.getDefault(), getString(R.string.version_name_hint),
                        getString(R.string.monitor), App.Companion.getAppContext().getString(R.string.none_connected_state_hint)));
                setText(mTvSleepyVersionName, String.format(Locale.getDefault(), getString(R.string.version_name_hint),
                        getString(R.string.speed_sleeper), App.Companion.getAppContext().getString(R.string.none_connected_state_hint)));
            }
            mDivider.setVisibility(isShowAppDot || isShowMonitorDot || isShowSleepyDot ? View.VISIBLE : View.GONE);
            mAppVersionInfo.updateUpgradeInfo(isShowAppDot, null);
            mMonitorVersionInfo.updateUpgradeInfo(isShowMonitorDot, AppManager.getDeviceModel().getMonitorSn());
            mSleepVersionInfo.updateUpgradeInfo(isShowSleepyDot, AppManager.getDeviceModel().getSleepySn());
        });
    }

    private void setText(TextView tv, String text) {
        runUiThread(() -> tv.setText(text));
    }

    @Override
    public void onFailure(String error) {
        ToastHelper.show(error);
    }

    @Override
    public void onBegin() {
        mRefresh.setRefreshing(true);
    }

    @Override
    public void onFinish() {
        mRefresh.setRefreshing(false);
    }
}
