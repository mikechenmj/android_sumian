package com.sumian.sleepdoctor.widget.calendar;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/29 20:30
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class CalendarWrapperAdapter extends RecyclerView.Adapter<CalendarWrapperVH> {

    private List<Long> mMonths = new ArrayList<>();

    @NonNull
    @Override
    public CalendarWrapperVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return CalendarWrapperVH.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarWrapperVH holder, int position) {
        holder.mCalendarView.setMonthTime(mMonths.get(position));
    }

    @Override
    public int getItemCount() {
        return mMonths.size();
    }

    public void setMonths(List<Long> months) {
        mMonths = months;
    }

}
