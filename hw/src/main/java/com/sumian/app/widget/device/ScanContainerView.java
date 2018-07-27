package com.sumian.app.widget.device;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.sumian.app.R;
import com.sumian.app.app.AppManager;
import com.sumian.app.improve.device.adapter.DeviceAdapter;
import com.sumian.app.tab.device.bean.BlueDevice;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jzz
 * on 2017/9/28
 * <p>
 * desc:设备扫描容器
 */

public class ScanContainerView extends LinearLayout implements View.OnClickListener {

    @BindView(R.id.ib_device_refresh)
    ImageButton mIbDeviceRefresh;

    @BindView(R.id.recycler)
    RecyclerView mDeviceRecyclerView;

    private DeviceAdapter mAdapter;

    public ScanContainerView(Context context) {
        this(context, null);
    }

    public ScanContainerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScanContainerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setOnItemClickListener(DeviceAdapter.OnItemClickListener onItemClickListener) {
        this.mAdapter.setOnItemClickListener(onItemClickListener);
    }

    private void init(Context context) {
        ButterKnife.bind(LayoutInflater.from(context).inflate(R.layout.hw_lay_device_list, this, true));
        this.mAdapter = new DeviceAdapter();
        this.mDeviceRecyclerView.setAdapter(mAdapter);
        this.mDeviceRecyclerView.setItemAnimator(new DefaultItemAnimator());
        this.mDeviceRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        this.mDeviceRecyclerView.setSoundEffectsEnabled(true);
    }

    public void addDevice(BlueDevice blueDevice) {
        //mAdapter.modifyItem(blueDevice);
    }

    public void clearCacheDevice() {
        mAdapter.clear();
    }

    public void showScanningAnimation() {
        mIbDeviceRefresh.clearAnimation();
        RotateAnimation r = new RotateAnimation(-360.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        r.setDuration(1500);
        r.setInterpolator(new AccelerateDecelerateInterpolator());
        r.setRepeatCount(Animation.INFINITE);
        mIbDeviceRefresh.startAnimation(r);
    }

    public void cancelScanningAnimation() {
        mIbDeviceRefresh.clearAnimation();
    }

    @OnClick(R.id.ib_device_refresh)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_device_refresh:
                AppManager.getBlueManager().doScan();
                break;
            default:
                break;
        }

    }
}
