package com.sumian.app.widget.device;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.sumian.sleepdoctor.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jzz
 * on 2017/9/28
 * <p>
 * desc:设备连接状态容器
 */

public class ConnectedContainerView extends LinearLayout {

    DeviceIndicatorView mDiv;
    DeviceStateItemView mDivMonitor;
    DeviceStateItemView mDivQuickSleeping;
    SwitchPowerView mSwitchPowerView;
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
        View inflate = LayoutInflater.from(context).inflate(R.layout.hw_lay_device_connected, this, true);
        mDiv = inflate.findViewById(R.id.div);
        mDivMonitor = inflate.findViewById(R.id.div_monitor);
        mDivQuickSleeping = inflate.findViewById(R.id.div_quick_sleeping);
        mSwitchPowerView = inflate.findViewById(R.id.switch_power_view);
        mSwitchModeView = inflate.findViewById(R.id.switch_pa_mode_view);
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
