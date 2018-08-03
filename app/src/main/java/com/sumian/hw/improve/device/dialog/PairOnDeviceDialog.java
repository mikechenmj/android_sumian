package com.sumian.hw.improve.device.dialog;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumian.blue.callback.BlueAdapterCallback;
import com.sumian.blue.callback.BlueScanCallback;
import com.sumian.blue.manager.BlueManager;
import com.sumian.common.helper.ToastHelper;
import com.sumian.hw.improve.device.bean.BlueDevice;
import com.sumian.hw.improve.device.util.BluetoothDeviceUtil;
import com.sumian.hw.improve.widget.device.DeviceListView;
import com.sumian.hw.improve.widget.device.DeviceScanErrorView;
import com.sumian.hw.improve.widget.device.DeviceScanView;
import com.sumian.hw.log.LogManager;
import com.sumian.sleepdoctor.BuildConfig;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.app.HwAppManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;

/**
 * Created by sm
 * on 2018/3/22.
 * desc:
 */

public class PairOnDeviceDialog extends AppCompatDialog implements View.OnClickListener, BlueScanCallback, DeviceScanErrorView.OnDeviceScanErrorCallback, DeviceListView.OnDeviceListViewCallback, DeviceScanView.OnDeviceScanningCallback, BlueAdapterCallback {

    @SuppressWarnings("unused")
    private static final String TAG = PairOnDeviceDialog.class.getSimpleName();

    public static final String ACTION_BIND = "com.sumian.app.intent.action.BIND_DEVICE";
    public static final String EXTRA_DEVICE = "com.sumian.app.intent.extra_DEVICE";

    private TextView mTvLabelH1;
    private TextView mTvLabelH2;
    private ImageView mIvDismiss;
    private DeviceScanErrorView mDeviceScanErrorView;
    private DeviceListView mDeviceListView;
    private DeviceScanView mDeviceScanView;

    private List<BlueDevice> mScanDevices = new ArrayList<>(0);

    private BlueManager mBlueManager;

    //private Unbinder mUnBinder;

    public PairOnDeviceDialog(Context context) {
        this(context, R.style.full_screen_dialog);
    }

    private PairOnDeviceDialog(Context context, int theme) {
        super(context, theme);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0 全透明实现
            if (window != null) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                //window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                window.setStatusBarColor(Color.TRANSPARENT);
            }
        } else {//4.4 全透明状态栏
            if (window != null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        @SuppressLint("InflateParams") View rootView = LayoutInflater.from(getContext()).inflate(R.layout.hw_lay_device_pair_view, null, false);
        ButterKnife.bind(this, rootView);
        setContentView(rootView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mTvLabelH1 = rootView.findViewById(R.id.tv_label_h1);
        mTvLabelH2 = rootView.findViewById(R.id.tv_label_h2);
        mIvDismiss = rootView.findViewById(R.id.iv_dismiss);
        mDeviceScanErrorView = rootView.findViewById(R.id.device_scan_error_view);
        mDeviceListView = rootView.findViewById(R.id.device_list_view);
        mDeviceScanView = rootView.findViewById(R.id.device_scanning_view);

        rootView.findViewById(R.id.iv_dismiss).setOnClickListener(this);

        mDeviceScanErrorView.setOnCallback(this);
        mDeviceListView.setOnDeviceListViewCallback(this);
        mDeviceScanView.setOnDeviceScanningCallback(this);

        showScanningView();

        this.mBlueManager = HwAppManager.getBlueManager();
        this.mBlueManager.addBlueScanCallback(this);
        this.mBlueManager.addBlueAdapterCallback(this);
        this.mBlueManager.doScanDelay();
        LogManager.appendBluetoothLog("第一次进入搜索界面,开始进行蓝牙搜索");
    }

    @Override
    public void onClick(View v) {
        this.mBlueManager.removeBlueScanCallback(this);
        this.mBlueManager.removeBlueAdapterCallback(this);
        this.mBlueManager.doStopScan();
        LogManager.appendBluetoothLog("退出搜索界面,停止蓝牙搜索");
        cancel();
    }

    @Override
    public void onLeScanCallback(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (rssi <= -80) {
            return;
        }
        BlueDevice blueDevice = new BlueDevice();
        blueDevice.name = device.getName();
        blueDevice.mac = device.getAddress();
        blueDevice.rssi = rssi;
        boolean isDeviceVersionValid = isDeviceValid(scanRecord);
        LogManager.appendBluetoothLog(
                String.format(Locale.getDefault(),
                        "搜索到 %s %s, isVersionValid: %b",
                        device.getName(),
                        device.getAddress(),
                        isDeviceVersionValid));
        if (isDeviceVersionValid) {
            if (mScanDevices.contains(blueDevice)) {
                return;
            }
            mScanDevices.add(blueDevice);
        }
    }

    /**
     * Release app only show release version device,
     * Clinical app show clinical version, release version, and old version device
     *
     * @param scanRecord device Advertising data
     * @return whether the device is valid for the app
     */
    @SuppressWarnings("SimplifiableIfStatement")
    private boolean isDeviceValid(byte[] scanRecord) {
        int deviceVersion = BluetoothDeviceUtil.getBluetoothDeviceVersion(scanRecord);
        if (BuildConfig.IS_CLINICAL_VERSION) { // clinical version app
            return true;
        } else { // release version app
            return deviceVersion == BluetoothDeviceUtil.BLUETOOTH_DEVICE_VERSION_RELEASE;
        }
    }

    @Override
    public void onBeginScanCallback() {
        invalidLabel(R.string.scanning_device, R.string.scan_label_h2);
        mScanDevices.clear();
        showScanningView();
        LogManager.appendBluetoothLog("开始进行蓝牙搜索");
    }

    @Override
    public void onFinishScanCallback() {
        switch (mScanDevices.size()) {
            case 0://一台设备都没有搜索到
                mTvLabelH1.setText("没有看到您的设备？");
                mTvLabelH2.setVisibility(View.INVISIBLE);

                mDeviceScanView.hide();
                mDeviceListView.hide();
                mDeviceScanErrorView.show();
                LogManager.appendBluetoothLog("该次没有搜索到任何设备");
                break;
            case 1://只搜索到一台设备
                invalidLabel(mScanDevices.get(0).name, getContext().getResources().getString(R.string.is_sure_device_2_bind));

                mDeviceListView.hide();
                mDeviceScanErrorView.hide();
                mDeviceScanView.showScanDevice(mScanDevices.get(0));
                LogManager.appendBluetoothLog("该次搜索到一台设备 " + mScanDevices.get(0).name + " " + mScanDevices.get(0).mac);
                break;
            default://搜索到>=2台设备
                invalidLabel(R.string.bind_device, R.string.select_bind_device_label);

                mDeviceScanView.hide();
                mDeviceScanErrorView.hide();
                mDeviceListView.showDevices(mScanDevices);
                LogManager.appendBluetoothLog("该次搜索到" + mScanDevices.size() + "台设备 " + mScanDevices.toString());
                break;
        }
    }

    @Override
    public void doBindDevice(BlueDevice blueDevice) {
        Intent intent = new Intent(ACTION_BIND);
        intent.putExtra(EXTRA_DEVICE, blueDevice);
        boolean sendBroadcast = LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
        if (sendBroadcast) {
            this.mBlueManager.removeBlueScanCallback(this);
            this.mBlueManager.removeBlueAdapterCallback(this);
            cancel();
        }
    }

    @Override
    public void doReScan() {
        showScanningView();
        mBlueManager.doScanDelay();
        LogManager.appendBluetoothLog("重新搜索蓝牙设备");
    }

    private void invalidLabel(String labelH1, String labelH2) {
        mTvLabelH1.setText(labelH1);
        mTvLabelH1.setVisibility(View.VISIBLE);
        mTvLabelH2.setText(labelH2);
        mTvLabelH2.setVisibility(View.VISIBLE);
    }

    private void invalidLabel(@StringRes int labelH1, @StringRes int labelH2) {
        invalidLabel(getContext().getString(labelH1), getContext().getString(labelH2));
    }

    private void showScanningView() {
        mDeviceListView.hide();
        mDeviceScanErrorView.hide();
        mDeviceScanView.showScanningStatus();
    }

    @Override
    public void onAdapterEnable() {
        ToastHelper.show("蓝牙已成功开启");
    }

    @Override
    public void onAdapterDisable() {
        ToastHelper.show("蓝牙已关闭,无法扫描或连接设备");
    }
}
