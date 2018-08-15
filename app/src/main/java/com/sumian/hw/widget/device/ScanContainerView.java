package com.sumian.hw.widget.device;

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

import com.sumian.hw.improve.device.adapter.DeviceAdapter;
import com.sumian.hw.tab.device.bean.BlueDevice;
import com.sumian.sd.R;
import com.sumian.sd.app.AppManager;

/**
 * Created by jzz
 * on 2017/9/28
 * <p>
 * desc:设备扫描容器
 */

public class ScanContainerView extends LinearLayout implements View.OnClickListener {

    ImageButton mIbDeviceRefresh;
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
        View inflate = LayoutInflater.from(context).inflate(R.layout.hw_lay_device_list, this, true);
        mIbDeviceRefresh = inflate.findViewById(R.id.ib_device_refresh);
        mDeviceRecyclerView = inflate.findViewById(R.id.recycler);
        inflate.findViewById(R.id.ib_device_refresh).setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        AppManager.getBlueManager().doScan();
    }
}
