package com.sumian.hw.improve.device.sheet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.TextView;

import com.sumian.hw.improve.device.bean.BlueDevice;
import com.sumian.hw.log.LogManager;
import com.sumian.hw.widget.BottomSheetView;
import com.sumian.sd.R;

/**
 * Created by jzz
 * on 2017/10/5
 * <p>
 * desc:
 */

public class DeviceBottomSheet extends BottomSheetView implements View.OnClickListener {

    private static final String TAG = DeviceBottomSheet.class.getSimpleName();
    public static final String ARGS_MONITOR = "args_monitor";
    public static final String ACTION_TURN_MONITORING_MODE = "com.sumian.app.action.TURN_MONITORING_MODE";
    public static final String ACTION_UNBIND = "com.sumian.app.action.UNBIND_DEVICE";
    public static final String EXTRA_MONITORING_MODE = "com.sumian.app.extra.MONITORING_MODE";

    TextView mTvMonitoringMode;
    View mVDividerOne;

    private BlueDevice mMonitor;

    public static DeviceBottomSheet newInstance() {
        return new DeviceBottomSheet();
    }

    @Override
    protected void initBundle(Bundle arguments) {
        super.initBundle(arguments);
        mMonitor = (BlueDevice) arguments.getSerializable(ARGS_MONITOR);
    }

    @Override
    protected int getLayout() {
        return R.layout.hw_lay_bottom_sheet_device;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mTvMonitoringMode = rootView.findViewById(R.id.tv_monitor_monitoring_mode);
        mVDividerOne = rootView.findViewById(R.id.v_divider_one);
        rootView.findViewById(R.id.tv_monitor_monitoring_mode).setOnClickListener(this);
        rootView.findViewById(R.id.tv_unbind_device).setOnClickListener(this);
        rootView.findViewById(R.id.tv_cancel).setOnClickListener(this);

        mTvMonitoringMode.setText(mMonitor != null && mMonitor.isMonitoring ? R.string.turn_off_snoop_mode : R.string.turn_on_snoop_mode);
        mTvMonitoringMode.setTextColor(getResources().getColor(mMonitor != null && mMonitor.isMonitoring ? R.color.dot_red_color : R.color.bt_hole_color));
        mTvMonitoringMode.setVisibility(mMonitor != null && mMonitor.isConnected() ? View.VISIBLE : View.GONE);
        mVDividerOne.setVisibility(mMonitor != null && mMonitor.isConnected() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        int i = view.getId();
        if (i == R.id.tv_monitor_monitoring_mode) {
            String text = mTvMonitoringMode.getText().toString().trim();
            intent.setAction(ACTION_TURN_MONITORING_MODE);
            if (getString(R.string.turn_on_snoop_mode).equals(text)) {//未开启监测模式情况
                intent.putExtra(EXTRA_MONITORING_MODE, 0x01);
                LogManager.appendUserOperationLog("打开监测仪的监测模式");
            } else {//已开启监测模式情况
                intent.putExtra(EXTRA_MONITORING_MODE, 0x00);
                LogManager.appendUserOperationLog("关闭监测仪的监测模式");
            }
            LocalBroadcastManager.getInstance(view.getContext()).sendBroadcast(intent);

        } else if (i == R.id.tv_unbind_device) {
            intent.setAction(ACTION_UNBIND);
            LocalBroadcastManager.getInstance(view.getContext()).sendBroadcast(intent);
            LogManager.appendUserOperationLog("解绑已连接的蓝牙设备");

        }
        dismiss();
    }
}
