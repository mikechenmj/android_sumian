package com.sumian.app.widget.device;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;
import com.sumian.app.utils.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sm
 * on 2018/3/5.
 * desc:设备状态 view
 */

public class DeviceStateItemView extends LinearLayout {

    TextView mTvDevice;
    View mWorkingIndicator;
    BatteryView mBv;
    View mVDivider;

    private int mDeviceConnected;

    private String mDeviceName;

    public DeviceStateItemView(Context context) {
        this(context, null);
    }

    public DeviceStateItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeviceStateItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        initView(context);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DeviceStateItemView);
        this.mDeviceName = typedArray.getString(R.styleable.DeviceStateItemView_device_name);
        typedArray.recycle();
    }

    private void initView(Context context) {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);

        View inflate = inflate(context, R.layout.hw_lay_device_state_item, this);
        mTvDevice = inflate.findViewById(R.id.tv_device);
        mWorkingIndicator = inflate.findViewById(R.id.working_indicator);
        mBv = inflate.findViewById(R.id.bv);
        mVDivider = inflate.findViewById(R.id.v_divider);

        String sleepyState = this.mDeviceName + " ";
        sleepyState += (mDeviceConnected == 0x01 ? StringUtils.getText(R.string.connected_state_hint) : StringUtils.getText(R.string.none_connected_state_hint));
        this.mTvDevice.setText(sleepyState);
    }

    public void updateConnectedState(int state) {
        this.mDeviceConnected = state;
        String sleepyState = this.mDeviceName + " ";
        sleepyState += (state == 0x01 ? StringUtils.getText(R.string.connected_state_hint) : StringUtils.getText(R.string.none_connected_state_hint));
        this.mTvDevice.setText(sleepyState);
        this.mVDivider.setVisibility(state == 0x01 ? VISIBLE : GONE);
        if (state != 0x01) {
            updateBattery(0);
            updatePower(0);
        }
    }

    public void updateBattery(int battery) {
        mBv.setAh(battery);
    }

    public void updatePower(int power) {
        String sleepyState = this.mDeviceName + " ";
        switch (power) {
            case 0x01://助眠功能档位为弱
            case 0x02://助眠功能档位为强
                sleepyState += StringUtils.getText(R.string.working_state_hint);
                break;
            case 0x00://助眠功能关闭状态
            default:
                sleepyState += StringUtils.getText(mDeviceConnected == 0x00 ? R.string.none_connected_state_hint : R.string.connected_state_hint);
                break;
        }
        this.mVDivider.setVisibility(mDeviceConnected == 0x01 || power >= 0x00 ? VISIBLE : GONE);
        this.mTvDevice.setText(sleepyState);
    }

    public void hide() {
        setVisibility(GONE);
    }

    public void show() {
        setVisibility(VISIBLE);
    }

}
