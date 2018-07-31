package com.sumian.hw.improve.device.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qmuiteam.qmui.util.QMUISpanHelper;
import com.sumian.sleepdoctor.R;
import com.sumian.hw.improve.device.bean.BlueDevice;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by jzz
 * on 2017/8/16.
 * desc:扫描到的设备信息列表
 */

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {

    private static final String TAG = DeviceAdapter.class.getSimpleName();

    private List<BlueDevice> mBlueDevices;
    private OnItemClickListener mOnItemClickListener;

    public DeviceAdapter() {
        this.mBlueDevices = new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.hw_lay_item_scan_device, parent, false));
        viewHolder.itemView.setTag(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BlueDevice item = this.mBlueDevices.get(position);
        holder.initView(item, mOnItemClickListener);
    }

    @Override
    public int getItemCount() {
        return this.mBlueDevices == null ? 0 : this.mBlueDevices.size();
    }

    public void modifyItem(BlueDevice blueDevice) {
        int index = getPosition(blueDevice);
        if (index == -1) {//未找到,直接添加 item
            int insertPosition = this.mBlueDevices.size();
            this.mBlueDevices.add(blueDevice);
            //第一次进来先按 rssi 排序
            // Collections.sort(this.mBlueDevices);
            notifyItemInserted(insertPosition);
        } else {//已存在,但是相关信息不一样,直接更新 item
            if (!this.mBlueDevices.contains(blueDevice)) {
                this.mBlueDevices.set(index, blueDevice);
                notifyItemChanged(index);
            }
        }
    }

    public void addData(List<BlueDevice> devices) {
        int insertPosition = this.mBlueDevices.size();
        this.mBlueDevices.addAll(devices);
        notifyItemRangeInserted(insertPosition, devices.size());
    }

    public void clear() {
        if (this.mBlueDevices.isEmpty()) return;
        int removeSize = this.mBlueDevices.size();
        this.mBlueDevices.clear();
        notifyItemRangeRemoved(0, removeSize);
    }

    public BlueDevice getItem(int position) {
        return this.mBlueDevices.get(position);
    }

    private int getPosition(BlueDevice blueDevice) {
        for (int i = 0, size = this.mBlueDevices.size(); i < size; i++) {
            if (this.mBlueDevices.get(i).mac.equals(blueDevice.mac)) {
                return i;
            }
        }
        return -1;
    }

    public interface OnItemClickListener {

        void onItemClick(View v, int position, BlueDevice blueDevice);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTvDeviceName;

        ViewHolder(View itemView) {
            super(itemView);
            mTvDeviceName = itemView.findViewById(R.id.tv_device);
        }

        void initView(BlueDevice blueDevice, OnItemClickListener onItemClickListener) {
            CharSequence charSequence = QMUISpanHelper.generateSideIconText(true, itemView.getResources().getDimensionPixelSize(R.dimen.space_20), blueDevice.name, itemView.getResources().getDrawable(R.mipmap.equip_icon_bracelet));
            mTvDeviceName.setText(charSequence);
            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(v, getAdapterPosition(), blueDevice));
        }
    }
}
