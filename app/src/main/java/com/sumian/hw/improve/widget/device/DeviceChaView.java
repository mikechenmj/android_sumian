package com.sumian.hw.improve.widget.device;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumian.hw.improve.device.bean.BlueDevice;
import com.sumian.hw.widget.device.BatteryView;
import com.sumian.sd.R;

/**
 * Created by sm
 * on 2018/3/5.
 * desc:设备状态 view
 */

public class DeviceChaView extends LinearLayout {

    TextView mTvName;
    TextView mTvStatus;
    TextView mTvSync;
    BatteryView mBv;
    View mVDivider;

    private int mStatus;//0x00  未连接  0x01  连接中  0x02  在线  0x03 同步数据状态  0x04 工作中
    private String mName;

    private BlueDevice mBlueDevice;

    public DeviceChaView(Context context) {
        this(context, null);
    }

    public DeviceChaView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeviceChaView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        initView(context);
        this.mBlueDevice = new BlueDevice();
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DeviceChaView);
        this.mName = typedArray.getString(R.styleable.DeviceChaView_name);
        this.mStatus = typedArray.getIndex(R.styleable.DeviceChaView_status);
        typedArray.recycle();
    }

    private void initView(Context context) {
        setGravity(Gravity.CENTER);
        setOrientation(VERTICAL);
        View inflate = inflate(context, R.layout.hw_lay_device_cha_view, this);

        mTvName = inflate.findViewById(R.id.tv_name);
        mTvStatus = inflate.findViewById(R.id.tv_status);
        mTvSync = inflate.findViewById(R.id.tv_sync);
        mBv = inflate.findViewById(R.id.bv);
        mVDivider = inflate.findViewById(R.id.v_divider);

        mTvName.setText(mName);
        if (mTvName.getText().toString().trim().equals(getResources().getString(R.string.monitor))) {
//            mTvSync.setVisibility(VISIBLE);
            mVDivider.setVisibility(VISIBLE);
        } else {
            mTvSync.setVisibility(INVISIBLE);
            mVDivider.setVisibility(GONE);
        }
        if (mStatus == 0x00) {
            mTvStatus.setText(R.string.none_connected_state_hint);
            mTvSync.setVisibility(INVISIBLE);
        }
    }

    public void setOnSyncSleepChaListener(OnClickListener onClickListener) {
        this.mTvSync.setOnClickListener(onClickListener);
    }

    public BlueDevice getBlueDevice() {
        return mBlueDevice;
    }

    @SuppressWarnings({"unused", "UnusedAssignment"})
    public void invalidDeice(BlueDevice blueDevice) {
        if (blueDevice == null) {
            return;
        }
        mTvName.setText(blueDevice.name);
        mBlueDevice = blueDevice;
        @ColorRes int statusColor;
        @StringRes int statusText;
        int isVisible;
        switch (blueDevice.status) {
            case BlueDevice.STATUS_CONNECTING://正在连接中
                statusText = R.string.connecting_state_hint;
                statusColor = R.color.bt_hole_color;
                isVisible = View.INVISIBLE;
                break;
            case BlueDevice.STATUS_CONNECTED://在线状态
                if(blueDevice.isMonitoring) {
                    statusText = R.string.connected_state_hint;
                    statusColor = R.color.bt_hole_color;
                    isVisible = View.VISIBLE;
                } else {
                    statusText = R.string.connected_state_hint;
                    statusColor = R.color.bt_hole_color;
                    isVisible = getResources().getString(R.string.speed_sleeper).equals(blueDevice.name) ? View.INVISIBLE : View.VISIBLE;
                }
                break;
            case BlueDevice.STATUS_SYNCHRONIZING://同步数据状态
                statusText = R.string.syncing;
                statusColor = R.color.bt_hole_color;
                isVisible = View.INVISIBLE;
                break;
            case BlueDevice.STATUS_PA://工作状态
                statusText = R.string.working_state_hint;
                statusColor = R.color.bt_hole_color;
                isVisible = View.INVISIBLE;
                break;
            case BlueDevice.STATUS_UNCONNECTED://未在线,未连接状态
            default:
                statusColor = R.color.full_general_color;
                statusText = R.string.none_connected_state_hint;
                isVisible = View.INVISIBLE;
                break;
        }
        mTvName.setTextColor(getResources().getColor(statusColor));
        mTvStatus.setText(statusText);
        mBv.setAh(blueDevice.battery);
    }

    public void hide() {
        setVisibility(GONE);
    }

    public void invisible() {
        setVisibility(INVISIBLE);
    }

    public void show() {
        setVisibility(VISIBLE);
    }
}
