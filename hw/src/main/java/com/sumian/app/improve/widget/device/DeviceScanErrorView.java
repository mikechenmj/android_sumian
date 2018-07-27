package com.sumian.app.improve.widget.device;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.sumian.app.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sm
 * on 2018/3/23.
 * <p>
 * desc:
 */

public class DeviceScanErrorView extends LinearLayout implements View.OnClickListener {

    private OnDeviceScanErrorCallback mCallback;

    public DeviceScanErrorView(Context context) {
        this(context, null);
    }

    public DeviceScanErrorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeviceScanErrorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateView(context);
    }

    private void inflateView(Context context) {
        ButterKnife.bind(inflate(context, R.layout.hw_lay_device_scan_error_view, this));
    }

    public void setOnCallback(OnDeviceScanErrorCallback callback) {
        mCallback = callback;
    }

    @OnClick(R.id.bt_go_connect)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_go_connect:
                mCallback.doReScan();
                break;
            default:
                break;
        }
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }

    public interface OnDeviceScanErrorCallback {

        void doReScan();
    }
}
