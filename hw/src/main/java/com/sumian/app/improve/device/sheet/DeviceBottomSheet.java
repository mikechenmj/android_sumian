package com.sumian.app.improve.device.sheet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.TextView;

import com.sumian.app.R;
import com.sumian.app.improve.device.bean.BlueDevice;
import com.sumian.app.log.LogManager;
import com.sumian.app.widget.BottomSheetView;

import butterknife.BindView;
import butterknife.OnClick;

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

    @BindView(R.id.tv_monitor_monitoring_mode)
    TextView mTvMonitoringMode;

    @BindView(R.id.v_divider_one)
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
    protected void initData() {
        super.initData();
        if (mMonitor != null) {
            if (mMonitor.status > 0x01) {
                switch (mMonitor.status) {
                    case 0x02:
                    case 0x03:
                    case 0x04:
                        mTvMonitoringMode.setText(R.string.turn_on_snoop_mode);
                        mTvMonitoringMode.setTextColor(getResources().getColor(R.color.bt_hole_color));
                        break;
                    case 0x05:
                        mTvMonitoringMode.setText(R.string.turn_off_snoop_mode);
                        mTvMonitoringMode.setTextColor(getResources().getColor(R.color.dot_red_color));
                        break;
                }
                mTvMonitoringMode.setVisibility(View.VISIBLE);
                mVDividerOne.setVisibility(View.VISIBLE);
            } else {
                mTvMonitoringMode.setVisibility(View.GONE);
                mVDividerOne.setVisibility(View.GONE);
            }
        } else {
            mTvMonitoringMode.setText(R.string.turn_on_snoop_mode);
            mTvMonitoringMode.setTextColor(getResources().getColor(R.color.bt_hole_color));
            mTvMonitoringMode.setVisibility(View.GONE);
            mVDividerOne.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.tv_monitor_monitoring_mode, R.id.tv_unbind_device, R.id.tv_cancel})
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.tv_monitor_monitoring_mode:
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
                break;
            case R.id.tv_unbind_device:
                intent.setAction(ACTION_UNBIND);
                LocalBroadcastManager.getInstance(view.getContext()).sendBroadcast(intent);
                LogManager.appendUserOperationLog("解绑已连接的蓝牙设备");
                break;
            case R.id.tv_cancel:
                break;
        }
        dismiss();
    }
}
