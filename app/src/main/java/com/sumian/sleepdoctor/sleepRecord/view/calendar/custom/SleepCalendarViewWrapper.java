package com.sumian.sleepdoctor.sleepRecord.view.calendar.custom;

import android.content.Context;

import com.blankj.utilcode.util.LogUtils;
import com.sumian.sleepdoctor.sleepRecord.bean.SleepRecordSummary;
import com.sumian.sleepdoctor.sleepRecord.view.calendar.calendarViewWrapper.CalendarViewWrapper;
import com.sumian.sleepdoctor.utils.TimeUtil;

import java.sql.Time;
import java.util.ArrayList;
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
        int dayType = SleepDayType.TYPE_NORMAL;
        if (timeInMillis == mTodayTime) {
            dayType = SleepDayType.TYPE_TODAY;
        } else if (timeInMillis > mTodayTime) {
            dayType = SleepDayType.TYPE_FEATURE;
        } else if (mHasSleepRecordDays.contains(timeInMillis)) {
            if (mHasDoctorEvaluationDays.contains(timeInMillis)) {
                dayType = SleepDayType.TYPE_HAS_RECORD_HAS_DOCTOR_EVALUATION;
            } else {
                dayType = SleepDayType.TYPE_HAS_RECORD_NO_DOCTOR_EVALUATION;
            }
        }
        if (timeInMillis == mSelectDayTime) {
            dayType = SleepDayType.TYPE_SELECTED_DAY;
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
//                LogUtils.d(TimeUtil.formatDate("yyyy/MM", time), time);
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
