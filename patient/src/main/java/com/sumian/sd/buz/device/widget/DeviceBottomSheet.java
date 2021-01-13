package com.sumian.sd.buz.device.widget;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.sumian.common.helper.ToastHelper;
import com.sumian.device.data.SumianDevice;
import com.sumian.device.manager.DeviceManager;
import com.sumian.sd.R;
import com.sumian.sd.buz.devicemanager.BlueDevice;
import com.sumian.sd.widget.base.BaseBottomSheetView;

/**
 * Created by jzz
 * on 2017/10/5
 * <p>
 * desc:
 */

public class DeviceBottomSheet extends BaseBottomSheetView implements View.OnClickListener {

    private static final String TAG = DeviceBottomSheet.class.getSimpleName();

    public static final String ARGS_MONITOR = "args_monitor";

    public static final String ACTION_TURN_MONITORING_MODE = "com.sumian.app.action.TURN_MONITORING_MODE";
    public static final String ACTION_UNBIND = "com.sumian.app.action.UNBIND_DEVICE";

    public static final String EXTRA_MONITORING_MODE = "com.sumian.app.extra.MONITORING_MODE";

    TextView mTvMonitoringMode;

    View mVDividerOne;

    public static DeviceBottomSheet newInstance() {
        return new DeviceBottomSheet();
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mTvMonitoringMode = rootView.findViewById(R.id.tv_monitor_monitoring_mode);
        mVDividerOne = rootView.findViewById(R.id.v_divider_one);
        mTvMonitoringMode.setOnClickListener(this);
        rootView.findViewById(R.id.tv_unbind_device).setOnClickListener(this);
        rootView.findViewById(R.id.tv_cancel).setOnClickListener(this);
    }

    @Override
    protected void initBundle(Bundle arguments) {
        super.initBundle(arguments);
    }

    @Override
    protected int getLayout() {
        return R.layout.lay_bottom_sheet_device;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_monitor_monitoring_mode:
                SumianDevice device = DeviceManager.INSTANCE.getDevice();
                if ( device != null && device.isMonitorConnected()) {
                    ToastHelper.show("监测模式已开启");
                }else {
                    ToastHelper.show("设备未连接");
                }

                break;
            case R.id.tv_unbind_device:
                DeviceManager.INSTANCE.unbind();
                break;
            case R.id.tv_cancel:
                break;
        }
        dismiss();
    }
}
