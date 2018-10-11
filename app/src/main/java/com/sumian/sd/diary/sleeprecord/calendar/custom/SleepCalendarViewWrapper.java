package com.sumian.sd.diary.sleeprecord.calendar.custom;

import android.content.Context;

import com.sumian.sd.diary.sleeprecord.bean.SleepRecordSummary;
import com.sumian.sd.utils.TimeUtil;
import com.sumian.sd.diary.sleeprecord.calendar.calendarViewWrapper.CalendarViewWrapper;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    public static final int PRELOAD_THRESHOLD = 5;
    private Set<Long> mHasSleepRecordDays = new HashSet<>();
    private Set<Long> mHasDoctorEvaluationDays = new HashSet<>();
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

    private void addSleepRecordSummaries(List<SleepRecordSummary> sleepRecordSummaries) {
        for (SleepRecordSummary summary : sleepRecordSummaries) {
            long summaryDate = summary.getDateInMillis();
            mHasSleepRecordDays.add(summaryDate);
            if (summary.isHasDoctorsEvaluation()) {
                mHasDoctorEvaluationDays.add(summaryDate);
            }
        }
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
        boolean hasData = mHasSleepRecordDays.contains(timeInMillis);
        if (timeInMillis == mSelectDayTime) {
            dayType = hasData ? SleepDayType.SELECT_HAS_DATA : SleepDayType.SELECT_NO_DATA;
        } else if (timeInMillis > mTodayTime) {
            dayType = SleepDayType.FEATURE;
        } else {
            dayType = hasData ? SleepDayType.HAS_DATA : SleepDayType.NO_DATA;
        }
        return dayType;
    }

    public void addSleepRecordSummaries(Map<String, List<SleepRecordSummary>> map) {
        if (map == null) {
            return;
        }
        for (Map.Entry<String, List<SleepRecordSummary>> entry : map.entrySet()) {
            addSleepRecordSummaries(entry.getValue());
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnPageChanged(int oldPosition, int newPosition) {
        super.OnPageChanged(oldPosition, newPosition);
        int monthCount = getMonthTimes().size();
        if (newPosition > monthCount - PRELOAD_THRESHOLD) {
            if (mLoadMoreListener != null) {
                Long time = getMonthTimes().get(monthCount - 1);
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
}
