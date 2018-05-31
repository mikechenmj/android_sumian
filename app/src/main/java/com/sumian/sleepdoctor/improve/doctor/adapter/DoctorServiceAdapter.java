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



        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void initView(DoctorService doctorService) {
            super.initView(doctorService);


        }

        @Override
        protected void onItemClick(View v) {
            super.onItemClick(v);

        }
    }
}
