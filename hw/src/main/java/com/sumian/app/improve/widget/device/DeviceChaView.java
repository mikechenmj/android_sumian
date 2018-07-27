package com.sumian.app.improve.widget.device;

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

import com.sumian.app.R;
import com.sumian.app.improve.device.bean.BlueDevice;
import com.sumian.app.widget.device.BatteryView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sm
 * on 2018/3/5.
 * desc:设备状态 view
 */

public class DeviceChaView extends LinearLayout {

    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.tv_status)
    TextView mTvStatus;

    @BindView(R.id.tv_sync)
    TextView mTvSync;

    @BindView(R.id.bv)
    BatteryView mBv;

    @BindView(R.id.v_divider)
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
        ButterKnife.bind(inflate(context, R.layout.hw_lay_device_cha_view, this));
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
        if (blueDevice == null) return;
        mTvName.setText(blueDevice.name);
        mBlueDevice = blueDevice;
        @ColorRes int statusColor;
        @StringRes int statusText;
        int isVisible;
        switch (blueDevice.status) {
            case 0x01://正在连接中
                statusText = R.string.connecting_state_hint;
                statusColor = R.color.bt_hole_color;
                isVisible = View.INVISIBLE;
                break;
            case 0x02://在线状态
                statusText = R.string.connected_state_hint;
                statusColor = R.color.bt_hole_color;
                isVisible = getResources().getString(R.string.speed_sleeper).equals(blueDevice.name) ? View.INVISIBLE : View.VISIBLE;
                break;
            case 0x03://同步数据状态
                statusText = R.string.syncing;
                statusColor = R.color.bt_hole_color;
                isVisible = View.INVISIBLE;
                break;
            case 0x04://工作状态
                statusText = R.string.working_state_hint;
                statusColor = R.color.bt_hole_color;
                isVisible = View.INVISIBLE;
                break;
            case 0x05://监测模式
                statusText = R.string.connected_state_hint;
                statusColor = R.color.bt_hole_color;
                isVisible = View.VISIBLE;
                break;
            case 0x00://未在线,未连接状态
            default:
                statusColor = R.color.full_general_color;
                statusText = R.string.none_connected_state_hint;
                isVisible = View.INVISIBLE;
                break;
        }
        mTvName.setTextColor(getResources().getColor(statusColor));
        mTvStatus.setText(statusText);
//        mTvSync.setVisibility(isVisible);
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
