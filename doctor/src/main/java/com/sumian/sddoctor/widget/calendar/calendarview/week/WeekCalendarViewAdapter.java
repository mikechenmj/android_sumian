package com.sumian.sddoctor.widget.calendar.calendarview.week;

import android.text.format.DateUtils;
import android.view.ViewGroup;

import com.sumian.sddoctor.booking.widget.calendarview.BookingCalendarViewVH;
import com.sumian.sddoctor.util.TimeUtil;
import com.sumian.sddoctor.widget.calendar.calendarview.base.AbsCalendarView;
import com.sumian.sddoctor.widget.calendar.calendarview.base.AbsCalendarViewAdapter;
import com.sumian.sddoctor.widget.calendar.calendarview.base.CalendarViewVH;
import com.sumian.sddoctor.widget.calendar.calendarview.base.DayTypeProvider;

import java.util.Calendar;

import androidx.annotation.NonNull;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/29 15:58
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class WeekCalendarViewAdapter extends AbsCalendarViewAdapter {

    private long mWeekTime;
    private AbsCalendarView.OnDateClickListener mOnDateClickListener;
    private DayTypeProvider mDayTypeProvider;

    @NonNull
    @Override
    public CalendarViewVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return BookingCalendarViewVH.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewVH holder, int position) {
        final int dayInMonth = getDayInMonthByPosition(position);
        holder.setDay(dayInMonth, getDayType(position));
        holder.mTextView.setOnClickListener(v -> {
            if (mOnDateClickListener != null) {
                mOnDateClickListener.onDateClick(getDayTimeByPosition(position));
            }
        });
    }

    private int getDayInMonthByPosition(int position) {
        long dayTimeByPosition = getDayTimeByPosition(position);
        Calendar calendar = TimeUtil.getCalendar(dayTimeByPosition);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    private int getDayType(int position) {
        if (mDayTypeProvider == null) {
            return 0;
        } else {
            long time = getDayTimeByPosition(position);
            return mDayTypeProvider.getDayTypeByTime(time);
        }
    }

    private long getDayTimeByPosition(int position) {
        return mWeekTime + DateUtils.DAY_IN_MILLIS * position;
    }

    @Override
    public int getItemCount() {
        return 7;
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public void setOnDateClickListener(AbsCalendarView.OnDateClickListener onDateClickListener) {
        mOnDateClickListener = onDateClickListener;
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public void setDayTypeProvider(DayTypeProvider dayTypeProvider) {
        mDayTypeProvider = dayTypeProvider;
    }

    @Override
    public void setTime(long time) {
        mWeekTime = TimeUtil.getStartTimeOfTheWeek(time);
        notifyDataSetChanged();
    }
}
