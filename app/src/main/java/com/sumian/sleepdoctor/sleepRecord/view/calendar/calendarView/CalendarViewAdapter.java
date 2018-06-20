package com.sumian.sleepdoctor.sleepRecord.view.calendar.calendarView;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.LongSparseArray;
import android.view.ViewGroup;

import com.blankj.utilcode.util.LogUtils;
import com.sumian.sleepdoctor.utils.TimeUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/29 15:58
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class CalendarViewAdapter extends RecyclerView.Adapter<CalendarViewVH> {

    private static final int INVALID_DAY_IN_MONTH = 0;
    private int mWeekdayShift;
    private long mMonthTime;
    private CalendarView.OnDateClickListener mOnDateClickListener;
    private CalendarView.DayTypeProvider mDayTypeProvider;

    @NonNull
    @Override
    public CalendarViewVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return CalendarViewVH.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewVH holder, int position) {
        final int dayInMonth = getDayInMonthByPosition(position);
        holder.setDay(dayInMonth, getDayType(position));
        holder.mTextView.setOnClickListener(v -> {
            if (isDayInMonthInvalid(dayInMonth)) {
                return;
            }
            if (mOnDateClickListener != null) {
                mOnDateClickListener.onDateClick(getDayTimeByDayInMonth(dayInMonth));
            }
        });
    }

    private long getDayTimeByDayInMonth(int dayInMonth) {
        return mMonthTime + DateUtils.DAY_IN_MILLIS * (dayInMonth - 1);
    }

    /**
     * @param position adapter position
     * @return INVALID_DAY_IN_MONTH or dayInMonth
     */
    private int getDayInMonthByPosition(int position) {
        int dayInMonth = position - mWeekdayShift + 1;
        if (dayInMonth <= 0) {
            return INVALID_DAY_IN_MONTH;
        } else {
            return dayInMonth;
        }
    }

    private int getDayType(int position) {
        int dayInMonth = getDayInMonthByPosition(position);
        if (isDayInMonthInvalid(dayInMonth) || mDayTypeProvider == null) {
            return 0;
        } else {
            return mDayTypeProvider.getDayTypeByTime(getDayTimeByDayInMonth(dayInMonth));
        }
    }

    private boolean isDayInMonthInvalid(int dayInMonth) {
        return dayInMonth == INVALID_DAY_IN_MONTH;
    }

    @Override
    public int getItemCount() {
        if (mMonthTime == 0) {
            return 0;
        }
        return TimeUtil.getDayCountInTheMonth(mMonthTime) + mWeekdayShift;
    }

    public void setOnDateClickListener(CalendarView.OnDateClickListener onDateClickListener) {
        mOnDateClickListener = onDateClickListener;
    }

    private void initDayShift() {
        Calendar startDayOfMonth = TimeUtil.getStartDayOfMonth(mMonthTime);
        int dayOfWeek = startDayOfMonth.get(Calendar.DAY_OF_WEEK);
        mWeekdayShift = dayOfWeek - 1;
    }

    public void setDayTypeProvider(CalendarView.DayTypeProvider dayTypeProvider) {
        mDayTypeProvider = dayTypeProvider;
    }

    public void setMonthTime(long monthTime) {
        mMonthTime = TimeUtil.getStartDayOfMonth(monthTime).getTimeInMillis();
        initDayShift();
        notifyDataSetChanged();
    }
}
