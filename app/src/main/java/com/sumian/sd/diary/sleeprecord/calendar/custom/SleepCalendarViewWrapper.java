package com.sumian.sd.diary.sleeprecord.calendar.custom;

import android.content.Context;

import com.sumian.sd.diary.sleeprecord.calendar.calendarViewWrapper.CalendarViewWrapper;
import com.sumian.sd.utils.TimeUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/2 10:55
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class SleepCalendarViewWrapper extends CalendarViewWrapper {
    public static final int PRELOAD_THRESHOLD = 3;
    private Set<Long> mHasRecordDays = new HashSet<>();
    private long mSelectDayTime;
    private long mTodayTime;
    private LoadMoreListener mLoadMoreListener;

    public SleepCalendarViewWrapper(Context context) {
        super(context);
    }

    @Override
    protected void init() {
        super.init();
    }

    public void setSelectDayTime(long selectDayTime) {
        mSelectDayTime = TimeUtil.getDayStartTime(selectDayTime);
        scrollToTime(mSelectDayTime, false);
    }

    public void setTodayTime(long todayTime) {
        mTodayTime = TimeUtil.getDayStartTime(todayTime);
    }

    @Override
    public int getDayTypeByTime(long timeInMillis) {
        int dayType;
        boolean hasData = mHasRecordDays.contains(timeInMillis);
        if (timeInMillis == mSelectDayTime) {
            dayType = hasData ? SleepDayType.SELECT_HAS_DATA : SleepDayType.SELECT_NO_DATA;
        } else if (timeInMillis > mTodayTime) {
            dayType = SleepDayType.FEATURE;
        } else {
            dayType = hasData ? SleepDayType.HAS_DATA : SleepDayType.NO_DATA;
        }
        return dayType;
    }

    public void addHasDataDays(Set<Long> days) {
        mHasRecordDays.addAll(days);
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);
        if (position < PRELOAD_THRESHOLD) {
            if (mLoadMoreListener != null) {
                Long time = getMonthTimes().get(0);
                mLoadMoreListener.loadMore(time);
            }
        }
    }

    public void setLoadMoreListener(LoadMoreListener loadMoreListener) {
        mLoadMoreListener = loadMoreListener;
    }

    public interface LoadMoreListener {
        void loadMore(long time);
    }

    @Override
    public void addMonthTimes(List<Long> monthTimeList, boolean isInit) {
        super.addMonthTimes(monthTimeList, isInit);
        if(isInit) {
            scrollToTime(mSelectDayTime, false);
        }
    }
}
