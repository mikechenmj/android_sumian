package com.sumian.sleepdoctor.sleepRecord.view.calendar.calendarViewWrapper;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.sumian.sleepdoctor.sleepRecord.view.calendar.calendarView.CalendarView;

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

    private CalendarView.OnDateClickListener mOnDateClickListener;
    private List<Long> mMonthTimes = new ArrayList<>();
    private CalendarView.DayTypeProvider mDayTypeProvider;

    CalendarWrapperAdapter(CalendarView.DayTypeProvider dayTypeProvider) {
        mDayTypeProvider = dayTypeProvider;
    }

    @NonNull
    @Override
    public CalendarWrapperVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return CalendarWrapperVH.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarWrapperVH holder, int position) {
        holder.mCalendarView.setOnDateClickListener(mOnDateClickListener);
        holder.mCalendarView.setDayTypeProvider(mDayTypeProvider);
        holder.mCalendarView.setMonthTime(mMonthTimes.get(position));
    }

    @Override
    public int getItemCount() {
        return mMonthTimes == null ? 0 : mMonthTimes.size();
    }

    public void setOnDateClickListener(CalendarView.OnDateClickListener onDateClickListener) {
        mOnDateClickListener = onDateClickListener;
    }

    public void setMonthTimes(List<Long> monthTimes) {
        mMonthTimes = monthTimes;
        notifyDataSetChanged();
    }
}
