package com.sumian.sleepdoctor.widget.calendar;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.ViewGroup;

import com.blankj.utilcode.util.LogUtils;
import com.sumian.sleepdoctor.utils.TimeUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/29 15:58
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class CalendarAdapter extends RecyclerView.Adapter<CalendarVH> {
    private long mMonthTime;
    private int mWeekdayShift;
    private SparseIntArray mDayDayTypeMap = new SparseIntArray(31);
    private OnDateClickListener mOnDateClickListener;

    public interface OnDateClickListener {
        void onDateClick(long time);
    }

    @NonNull
    @Override
    public CalendarVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return CalendarVH.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarVH holder, int position) {
        final int day = getDayByPosition(position);
        holder.setDay(day, getTextType(position));
        holder.mTextView.setOnClickListener(v -> {
            if (day <= 0) {
                return;
            }
            Calendar calendar = TimeUtil.getStartDayOfMonth(mMonthTime);
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

    @CalendarView.DayType
    private int getTextType(int position) {
        if (position < mWeekdayShift) {
            return CalendarView.DAY_TYPE_NORMAL;
        } else {
            return getDayTypeByDay(getDayByPosition(position));
        }
    }

    @Override
    public int getItemCount() {
        if (mMonthTime == 0) {
            return 0;
        }
        return TimeUtil.getDayCountInTheMonth(mMonthTime) + mWeekdayShift;
    }

    public void setEmphasizeDays(@CalendarView.DayType int dayType, List<Integer> days) {
        for (Integer day : days) {
            mDayDayTypeMap.put(day, dayType);
        }
    }

    private int getDayTypeByDay(int day) {
        int dayType = mDayDayTypeMap.get(day);
        if (dayType == 0) {
            return CalendarView.DAY_TYPE_NORMAL;
        } else {
            return dayType;
        }
    }

    public void setMonthTime(long monthTime) {
        mMonthTime = monthTime;
        Calendar startDayOfMonth = TimeUtil.getStartDayOfMonth(mMonthTime);
        int dayOfWeek = startDayOfMonth.get(Calendar.DAY_OF_WEEK);
        mWeekdayShift = dayOfWeek - 1;
    }

    public void setOnDateClickListener(OnDateClickListener onDateClickListener) {
        mOnDateClickListener = onDateClickListener;
    }

}
