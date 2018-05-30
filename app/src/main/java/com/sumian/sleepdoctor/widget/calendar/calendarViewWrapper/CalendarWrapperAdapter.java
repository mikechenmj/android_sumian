package com.sumian.sleepdoctor.widget.calendar.calendarViewWrapper;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.sumian.sleepdoctor.widget.calendar.calendarView.CalendarViewAdapter;
import com.sumian.sleepdoctor.widget.calendar.calendarView.CalendarViewData;

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

    private CalendarViewAdapter.OnDateClickListener mOnDateClickListener;
    private List<CalendarViewData> mCalendarViewDataList = new ArrayList<>();

    @NonNull
    @Override
    public CalendarWrapperVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return CalendarWrapperVH.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarWrapperVH holder, int position) {
        holder.mCalendarView.setCalendarViewData(mCalendarViewDataList.get(position));
        holder.mCalendarView.setOnDateClickListener(mOnDateClickListener);
    }

    @Override
    public int getItemCount() {
        return mCalendarViewDataList == null ? 0 : mCalendarViewDataList.size();
    }

    public void setOnDateClickListener(CalendarViewAdapter.OnDateClickListener onDateClickListener) {
        mOnDateClickListener = onDateClickListener;
    }

    public void setData(List<CalendarViewData> dataList) {
        mCalendarViewDataList = dataList;
        notifyDataSetChanged();
    }
}
