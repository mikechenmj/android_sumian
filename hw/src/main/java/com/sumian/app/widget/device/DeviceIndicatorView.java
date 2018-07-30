package com.sumian.app.widget.device;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumian.app.R;
import com.sumian.app.app.App;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sm
 * on 2018/3/5.
 * desc:
 */

public class DeviceIndicatorView extends LinearLayout implements View.OnClickListener {

    TextView mTvDeviceName;
    ImageView mLoading;
    Button mBtSync;

    private OnDeviceIndicatorCallback mOnDeviceIndicatorCallback;

    public DeviceIndicatorView(Context context) {
        this(context, null);
    }

    public DeviceIndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeviceIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        setOrientation(VERTICAL);
        View inflate = inflate(context, R.layout.hw_lay_device_indicator, this);
        mTvDeviceName = inflate.findViewById(R.id.tv_device_name);
        mLoading = inflate.findViewById(R.id.loading);
        mBtSync = inflate.findViewById(R.id.bt_sync);
        inflate.findViewById(R.id.bt_sync).setOnClickListener(this);
    }

    public void setOnDeviceIndicatorCallback(OnDeviceIndicatorCallback onDeviceIndicatorCallback) {
        mOnDeviceIndicatorCallback = onDeviceIndicatorCallback;
    }

    public void setDeviceName(String deviceName) {
        this.mTvDeviceName.setText(deviceName);
    }

    public void showLoading(boolean isLoading) {
        if (isLoading) {
            App.getRequestManager().load(R.mipmap.ic_loading).asGif().into(mLoading);
        } else {
            App.getRequestManager().load(R.mipmap.ic_more).asBitmap().into(mLoading);
        }
        this.mLoading.setVisibility(isLoading ? VISIBLE : GONE);
        this.mBtSync.setText(R.string.sync);
        this.mBtSync.setVisibility(isLoading ? GONE : VISIBLE);
        if (!isLoading) {
            this.mBtSync.postDelayed(() -> mBtSync.setVisibility(VISIBLE), 2000);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (mOnDeviceIndicatorCallback != null) {
            this.mOnDeviceIndicatorCallback.requestSync();
        }
        doSync();
    }

    public void doSync() {
        this.mLoading.setVisibility(GONE);
        this.mBtSync.setVisibility(VISIBLE);
        this.mBtSync.setText(R.string.syncing);
        this.mBtSync.setEnabled(false);
    }

    public void undoSync() {
        this.mBtSync.setText(R.string.sync);
        this.mBtSync.setEnabled(true);
        this.mBtSync.setVisibility(VISIBLE);
    }

    public interface OnDeviceIndicatorCallback {

        void requestSync();
    }
}
