package com.sumian.hw.widget.device;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumian.sd.R;
import com.sumian.hw.device.bean.BlueDevice;
import com.sumian.hw.widget.ripple.RippleScanningView;

/**
 * Created by sm
 * on 2018/3/23.
 * <p>
 * desc:设备扫描界面
 */

public class DeviceScanView extends LinearLayout implements OnClickListener {

    ImageView mIvIcon;
    RippleScanningView mRipple;
    Button mBtBind;
    TextView mTvReScan;
    LinearLayout mLayShowErrorContainer;

    private BlueDevice mBlueDevice;

    private OnDeviceScanningCallback mCallback;

    public DeviceScanView(Context context) {
        this(context, null);
    }

    public DeviceScanView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeviceScanView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        inflateView(context);
    }

    public void setOnDeviceScanningCallback(OnDeviceScanningCallback callback) {
        mCallback = callback;
    }

    private void inflateView(Context context) {
        View inflate = inflate(context, R.layout.hw_lay_scan_view, this);
        mIvIcon = inflate.findViewById(R.id.iv_icon);
        mRipple = inflate.findViewById(R.id.ripple);
        mBtBind = inflate.findViewById(R.id.bt_bind);
        mTvReScan = inflate.findViewById(R.id.tv_re_scan);
        mLayShowErrorContainer = inflate.findViewById(R.id.lay_show_error_container);

        inflate.findViewById(R.id.bt_bind).setOnClickListener(this);
        inflate.findViewById(R.id.tv_re_scan).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.bt_bind) {
            mCallback.doBindDevice(mBlueDevice);
        } else if (i == R.id.tv_re_scan) {
            showScanningStatus();
            mCallback.doReScan();
        }
    }

    public void showScanDevice(BlueDevice blueDevice) {
        this.mBlueDevice = blueDevice;

        mIvIcon.clearAnimation();
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.5f, 1, 0.5f, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setRepeatCount(Animation.INFINITE);
        scaleAnimation.setRepeatMode(Animation.REVERSE);
        scaleAnimation.setDuration(1000);
        scaleAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        //mIvIcon.setAnimation(scaleAnimation);
        // mIvIcon.startAnimation(scaleAnimation);
        mIvIcon.setVisibility(VISIBLE);
        mBtBind.setVisibility(View.VISIBLE);
        mLayShowErrorContainer.setVisibility(View.VISIBLE);
        show();
    }

    public void showScanningStatus() {
        mRipple.startAnimation();
        mRipple.setVisibility(VISIBLE);
        mIvIcon.clearAnimation();
        mIvIcon.setVisibility(INVISIBLE);
        mBtBind.setVisibility(View.GONE);
        mLayShowErrorContainer.setVisibility(View.GONE);
        show();
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }

    public interface OnDeviceScanningCallback {

        void doBindDevice(BlueDevice device);

        void doReScan();
    }
}
