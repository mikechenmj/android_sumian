package com.sumian.sd.buz.upgrade.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.sumian.common.base.BaseViewModelActivity;
import com.sumian.common.widget.TitleBar;
import com.sumian.common.widget.refresh.SumianSwipeRefreshLayout;
import com.sumian.device.data.SumianDevice;
import com.sumian.device.manager.DeviceManager;
import com.sumian.sd.R;
import com.sumian.sd.buz.version.VersionManager;
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
        TitleBar.OnBackClickListener {

    private SumianSwipeRefreshLayout mRefresh;
    private TextView mTvMonitorVersionName;
    private TextView mTvSleepyVersionName;
    private VersionInfoView mMonitorVersionInfo;
    private VersionInfoView mSleepVersionInfo;

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

        mTvMonitorVersionName = findViewById(R.id.tv_monitor_version_name);
        mTvSleepyVersionName = findViewById(R.id.tv_sleepy_version_name);
        mMonitorVersionInfo = findViewById(R.id.monitor_version_info);
        mSleepVersionInfo = findViewById(R.id.sleepy_version_info);

        findViewById(R.id.monitor_version_info).setOnClickListener(this);
        findViewById(R.id.sleepy_version_info).setOnClickListener(this);
        VersionManager.INSTANCE.getMFirmwareVersionInfoLD().observe(this, firmwareInfo -> {
            mRefresh.setRefreshing(false);
            DeviceManager deviceManager = DeviceManager.INSTANCE;
            mMonitorVersionInfo.setVisibility(deviceManager.isMonitorConnected() ? View.VISIBLE : View.GONE);
            mSleepVersionInfo.setVisibility(deviceManager.isSleepMasterConnected() ? View.VISIBLE : View.GONE);
            SumianDevice device = deviceManager.getDevice();
            mMonitorVersionInfo.updateUpgradeInfo(VersionManager.INSTANCE.hasNewMonitorVersion(), device == null ? "" : device.getMonitorSn());
            mSleepVersionInfo.updateUpgradeInfo(VersionManager.INSTANCE.hasNewSleeperVersion(), device == null ? "" : device.getSleepMasterSn());

            mTvMonitorVersionName.setText(
                    String.format(Locale.getDefault(),
                            getString(R.string.version_name_hint),
                            getString(R.string.monitor),
                            getVersionString(device == null ? "" : DeviceManager.INSTANCE.getMonitorSoftwareVersion())));
            mTvSleepyVersionName.setText(
                    String.format(Locale.getDefault(),
                            getString(R.string.version_name_hint),
                            getString(R.string.speed_sleeper),
                            getVersionString(device == null ? "" : DeviceManager.INSTANCE.getSleepMasterSoftwareVersion())));

        });
    }

    @Override
    protected void initData() {
        super.initData();
        VersionManager.INSTANCE.queryDeviceVersion(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.monitor_version_info:
                DeviceVersionUpgradeActivity.Companion.show(this, DeviceVersionUpgradeActivity.TYPE_MONITOR, VersionManager.INSTANCE.hasNewMonitorVersion());
                break;
            case R.id.sleepy_version_info:
                DeviceVersionUpgradeActivity.Companion.show(this, DeviceVersionUpgradeActivity.TYPE_SLEEP_MASTER, VersionManager.INSTANCE.hasNewSleeperVersion());
                break;
        }
    }

    @Override
    public void onRefresh() {
        mRefresh.setRefreshing(true);
        VersionManager.INSTANCE.queryDeviceVersion(false);
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

    public void onFinish() {
        mRefresh.setRefreshing(false);
    }

    private String getVersionString(String version) {
        return version == null ? getString(R.string.none_connected_state_hint) : version;
    }
}
