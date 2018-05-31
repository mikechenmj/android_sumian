package com.sumian.sleepdoctor.improve.doctor.adapter;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.base.holder.BaseViewHolder;
import com.sumian.sleepdoctor.improve.doctor.bean.DoctorService;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by sm
 * on 2018/5/30 15:09
 * desc:
 **/
public class DoctorServiceAdapter extends RecyclerView.Adapter<DoctorServiceAdapter.ViewHolder> {

    private ArrayList<DoctorService> mItems = new ArrayList<>(0);

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.lay_item_doctor_service, parent, false));
        viewHolder.itemView.setTag(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.initView(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void addItems(ArrayList<DoctorService> items) {
        if (items == null || items.isEmpty()) return;
        int position = mItems.size();
        mItems.addAll(position, items);
        notifyItemRangeInserted(0, items.size());
    }

    class ViewHolder extends BaseViewHolder<DoctorService> {

        @BindView(R.id.iv_service_icon)
        QMUIRadiusImageView ivServiceIcon;
        @BindView(R.id.tv_service_name)
        TextView tvServiceName;
        @BindView(R.id.tv_service_desc)
        TextView tvServiceDesc;

        @BindView(R.id.lay_service_action)
        LinearLayout layServiceAction;
        @BindView(R.id.tv_service_use_desc)
        TextView tvServiceUseDesc;
        @BindView(R.id.tv_service_action)
        TextView tvServiceAction;

        @BindView(R.id.v_divider)
        View bottomDivider;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void initView(DoctorService doctorService) {
            super.initView(doctorService);

            @DrawableRes int serviceIconId = R.mipmap.ic_img_sleepdiary_avatar;
            @StringRes int nextActionId = R.string.ask_questions;
            switch (doctorService.getType()) {
                case DoctorService.GRAPHIC_SERVICE_TYPE:
                    serviceIconId = R.mipmap.ic_img_advisory_avatar;
                    nextActionId = R.string.ask_questions;
                    break;
                case DoctorService.SLEEP_REPORT_TYPE:
                    serviceIconId = R.mipmap.ic_img_sleepdiary_avatar;
                    nextActionId = R.string.go_records;
                    break;
                default:
                    break;
            }

            load(doctorService.getIcon(), serviceIconId, ivServiceIcon);

            tvServiceName.setText(doctorService.getName());
            tvServiceDesc.setText(doctorService.getNot_buy_description());

            if (doctorService.getLast_count() == 0 && doctorService.getDay_last() == 0 && doctorService.getExpired_at() == 0) {
                layServiceAction.setVisibility(View.GONE);
            } else {
                layServiceAction.setVisibility(View.VISIBLE);
            }

            tvServiceUseDesc.setText(doctorService.getRemaining_description());

            tvServiceAction.setText(nextActionId);

            bottomDivider.setVisibility(getAdapterPosition() == getItemCount() - 1 ? View.GONE : View.VISIBLE);
        }

        @Override
        protected void onItemClick(View v) {
            super.onItemClick(v);

        }
    }
}
