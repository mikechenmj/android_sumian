package com.sumian.app.widget.device;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.sumian.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jzz
 * on 2017/9/28
 * <p>
 * desc:设备连接状态容器
 */

public class ConnectedContainerView extends LinearLayout {

    @BindView(R.id.div)
    DeviceIndicatorView mDiv;

    @BindView(R.id.div_monitor)
    DeviceStateItemView mDivMonitor;

    @BindView(R.id.div_quick_sleeping)
    DeviceStateItemView mDivQuickSleeping;

    @BindView(R.id.switch_power_view)
    SwitchPowerView mSwitchPowerView;
    @BindView(R.id.switch_pa_mode_view)
    SwitchModeView mSwitchModeView;


    public ConnectedContainerView(Context context) {
        this(context, null);
    }

    public ConnectedContainerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ConnectedContainerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        ButterKnife.bind(LayoutInflater.from(context).inflate(R.layout.hw_lay_device_connected, this, true));
    }

    public void addOnSwitchPowerListener(SwitchPowerView.OnSwitchPowerListener onSwitchPowerListener) {
        this.mSwitchPowerView.setOnSwitchPowerListener(onSwitchPowerListener);
    }

    public ConnectedContainerView addOnSwitchPaModeListener(SwitchModeView.OnSwitchPaModeListener onSwitchPaModeListener) {
        this.mSwitchModeView.setOnSwitchPaModeListener(onSwitchPaModeListener);
        return this;
    }

    public void setOnDeviceIndicatorCallback(DeviceIndicatorView.OnDeviceIndicatorCallback deviceIndicatorCallback) {
        this.mDiv.setOnDeviceIndicatorCallback(deviceIndicatorCallback);
    }

    public void updateQuickSleepingPower(int power) {
        mDivQuickSleeping.updatePower(power);
    }

    public void setDeviceName(String deviceName) {
        this.mDiv.setDeviceName(deviceName);
    }

    public void doSync() {
        mDiv.doSync();
    }

    public void undoSync() {
        mDiv.undoSync();
    }

    public void updateMonitorConnectedState(int connectState) {
        this.mDivMonitor.updateConnectedState(connectState);
        if (connectState != 0x00) {
            this.mDivQuickSleeping.updateConnectedState(0x00);
            updateForSnoopingMode(0x00, 0x00, 0x00);
        }
    }

    public void updateQuickSleepingConnectedState(int connectState) {
        this.mDivQuickSleeping.updateConnectedState(connectState);
    }

    public void showConnectingLoading(boolean isShow) {
        this.mDiv.showLoading(isShow);
    }

    public void updateMonitorBattery(int battery) {
        mDivMonitor.updateBattery(battery);
    }


    public void updateSleepyBattery(int battery) {
        mDivQuickSleeping.updateBattery(battery);
    }

    public void updateForSnoopingMode(int snoopingModeState, int paModeState, int sleepyConnectedState) {
        this.mSwitchModeView.updateForSnoopingMode(snoopingModeState, paModeState, sleepyConnectedState);
    }
}
