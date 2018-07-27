package com.sumian.app.upgrade.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.TextView;

import com.sumian.app.R;
import com.sumian.app.app.App;
import com.sumian.app.app.AppManager;
import com.sumian.app.base.BaseActivity;
import com.sumian.app.common.helper.ToastHelper;
import com.sumian.app.common.util.UiUtil;
import com.sumian.app.network.response.AppUpgradeInfo;
import com.sumian.app.upgrade.bean.VersionInfo;
import com.sumian.app.upgrade.contract.VersionContract;
import com.sumian.app.upgrade.model.VersionModel;
import com.sumian.app.upgrade.presenter.VersionPresenter;
import com.sumian.app.widget.TitleBar;
import com.sumian.app.widget.VersionInfoView;
import com.sumian.app.widget.refresh.BlueRefreshView;
import com.sumian.blue.model.BluePeripheral;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jzz
 * on 2017/11/23.
 * <p>
 * desc:
 */

public class VersionNoticeActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
    TitleBar.OnBackListener, VersionContract.View, VersionModel.ShowDotCallback {

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;
    @BindView(R.id.refresh)
    BlueRefreshView mRefresh;

    @BindView(R.id.tv_app_version_name)
    TextView mTvAppVersionName;
    @BindView(R.id.tv_monitor_version_name)
    TextView mTvMonitorVersionName;
    @BindView(R.id.tv_sleepy_version_name)
    TextView mTvSleepyVersionName;

    @BindView(R.id.v_divider)
    View mDivider;

    @BindView(R.id.app_version_info)
    VersionInfoView mAppVersionInfo;

    @BindView(R.id.monitor_version_info)
    VersionInfoView mMonitorVersionInfo;

    @BindView(R.id.sleepy_version_info)
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

    @OnClick({R.id.app_version_info, R.id.monitor_version_info, R.id.sleepy_version_info})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.app_version_info:
                UiUtil.openAppInMarket(v.getContext());
                //VersionUpgradeActivity.show(this, VersionUpgradeActivity.VERSION_TYPE_APP, AppManager.getVersionModel().isShowAppVersionDot());
                break;
            case R.id.monitor_version_info:
                VersionUpgradeActivity.show(this, VersionUpgradeActivity.VERSION_TYPE_MONITOR, AppManager.getVersionModel().isShowMonitorVersionDot());
                break;
            case R.id.sleepy_version_info:
                VersionUpgradeActivity.show(this, VersionUpgradeActivity.VERSION_TYPE_SLEEPY, AppManager.getVersionModel().isShowSleepyVersionDot());
                break;
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
                    getString(R.string.monitor), App.getAppContext().getString(R.string.none_connected_state_hint)));
                setText(mTvSleepyVersionName, String.format(Locale.getDefault(), getString(R.string.version_name_hint),
                    getString(R.string.speed_sleeper), App.getAppContext().getString(R.string.none_connected_state_hint)));
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
