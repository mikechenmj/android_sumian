package com.sumian.sleepdoctor.widget.calendar.calendarView;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/29 15:58
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class CalendarViewAdapter extends RecyclerView.Adapter<CalendarViewVH> {

    private CalendarViewData mCalendarViewData;
    private int mWeekdayShift;
    @SuppressLint("UseSparseArrays")
    private Map<Integer, DayType> mDayDayTypeMap = new HashMap<>(31); // 存储day的类型， dayOfMonth -- dayType
    private OnDateClickListener mOnDateClickListener;

    public interface OnDateClickListener {
        void onDateClick(long time);
    }

    @NonNull
    @Override
    public CalendarViewVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return CalendarViewVH.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewVH holder, int position) {
        final int day = getDayByPosition(position);
        holder.setDay(day, getTextType(position));
        holder.mTextView.setOnClickListener(v -> {
            if (day <= 0) {
                return;
            }
            Calendar calendar = TimeUtil.getStartDayOfMonth(getMonthTime());
            calendar.roll(Calendar.DAY_OF_MONTH, day - 1);
            LogUtils.d(new Date(calendar.getTimeInMillis()));
            if (mOnDateClickListener != null) {
                mOnDateClickListener.onDateClick(calendar.getTimeInMillis());
            }
        });
    }

    /**
     * @param position adapter position
     * @return 0(invalid day) or normal day 1-31
     */
    private int getDayByPosition(int position) {
        int day = position - mWeekdayShift + 1;
        if (day <= 0) {
            return 0;
        } else {
            return day;
        }
    }

    private DayType getTextType(int position) {
        if (position < mWeekdayShift) {
            return DayType.NORMAL;
        } else {
            return getDayTypeByDay(getDayByPosition(position));
        }
    }

    @Override
    public int getItemCount() {
        if (getMonthTime() == 0) {
            return 0;
        }
        return TimeUtil.getDayCountInTheMonth(getMonthTime()) + mWeekdayShift;
    }

    private long getMonthTime() {
        if (mCalendarViewData == null) {
            return 0;
        }
        return mCalendarViewData.monthTime;
    }

    private DayType getDayTypeByDay(int day) {
        DayType dayType = mDayDayTypeMap.get(day);
        if (dayType == null) {
            return DayType.NORMAL;
        } else {
            return dayType;
        }
    }

    public void setOnDateClickListener(OnDateClickListener onDateClickListener) {
        mOnDateClickListener = onDateClickListener;
    }

    public void setCalendarViewData(CalendarViewData calendarViewData) {
        mCalendarViewData = calendarViewData;
        // init day shift
        initDayShift();
        // init emphasize days
        initEmphasizeDays();
        // notifyDataSetChanged
        notifyDataSetChanged();
    }

    private void initEmphasizeDays() {
        LongSparseArray<DayType> map = mCalendarViewData.dayDayTypeMap;
        for (int i = 0; i < map.size(); i++) {
            long dayTimeInMillis = map.keyAt(i);
            DayType dayType = map.get(dayTimeInMillis);
            int dayOfMonth = TimeUtil.getDayOfMonth(dayTimeInMillis);
            mDayDayTypeMap.put(dayOfMonth, dayType);
        }
    }

    private void initDayShift() {
        Calendar startDayOfMonth = TimeUtil.getStartDayOfMonth(getMonthTime());
        int dayOfWeek = startDayOfMonth.get(Calendar.DAY_OF_WEEK);
        mWeekdayShift = dayOfWeek - 1;
    }

}
