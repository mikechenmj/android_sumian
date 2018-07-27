package com.sumian.app.improve.widget.device;

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

import com.sumian.app.R;
import com.sumian.app.improve.device.bean.BlueDevice;
import com.sumian.app.improve.widget.ripple.RippleScanningView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sm
 * on 2018/3/23.
 * <p>
 * desc:设备扫描界面
 */

public class DeviceScanView extends LinearLayout implements OnClickListener {

    @BindView(R.id.iv_icon)
    ImageView mIvIcon;

    @BindView(R.id.ripple)
    RippleScanningView mRipple;

    @BindView(R.id.bt_bind)
    Button mBtBind;

    @BindView(R.id.tv_re_scan)
    TextView mTvReScan;

    @BindView(R.id.lay_show_error_container)
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
        ButterKnife.bind(inflate(context, R.layout.hw_lay_scan_view, this));
    }

    @OnClick({R.id.bt_bind, R.id.tv_re_scan})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_bind:
                mCallback.doBindDevice(mBlueDevice);
                break;
            case R.id.tv_re_scan:
                showScanningStatus();
                mCallback.doReScan();
                break;
            default:
                break;
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
