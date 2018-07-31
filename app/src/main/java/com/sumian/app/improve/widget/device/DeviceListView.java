package com.sumian.app.improve.widget.device;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.sumian.sleepdoctor.R;
import com.sumian.app.improve.device.adapter.DeviceAdapter;
import com.sumian.app.improve.device.bean.BlueDevice;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sm
 * on 2018/3/23.
 * <p>
 * desc:扫描设备列表容器
 */

public class DeviceListView extends LinearLayout implements View.OnClickListener, DeviceAdapter.OnItemClickListener {


    RecyclerView mRecycler;

    private DeviceAdapter mDeviceAdapter;

    private OnDeviceListViewCallback mCallback;

    public DeviceListView(Context context) {
        this(context, null);
    }

    public DeviceListView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeviceListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateView(context);
    }

    private void inflateView(Context context) {
        View inflate = inflate(context, R.layout.hw_lay_scanning_device_list_view, this);
        ButterKnife.bind(inflate);
        mRecycler = inflate.findViewById(R.id.recycler);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mRecycler.setAdapter(mDeviceAdapter = new DeviceAdapter());
        mDeviceAdapter.setOnItemClickListener(this);

        inflate.findViewById(R.id.tv_re_scan).setOnClickListener(this);
    }

    public void setOnDeviceListViewCallback(OnDeviceListViewCallback callback) {
        mCallback = callback;
    }

    @Override
    public void onClick(View v) {
        mDeviceAdapter.clear();
        mCallback.doReScan();
        hide();
    }


    public void showDevices(List<BlueDevice> devices) {
        Collections.sort(devices);
        mDeviceAdapter.addData(devices);
        show();
    }

    public void clearDevices() {
        mDeviceAdapter.clear();
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }

    @Override
    public void onItemClick(View v, int position, BlueDevice blueDevice) {
        mCallback.doBindDevice(blueDevice);
    }

    public interface OnDeviceListViewCallback {

        void doBindDevice(BlueDevice blueDevice);

        void doReScan();
    }
}
