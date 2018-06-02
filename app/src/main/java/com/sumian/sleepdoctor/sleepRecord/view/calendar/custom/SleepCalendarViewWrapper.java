package com.sumian.sleepdoctor.sleepRecord.view.calendar.custom;

import android.content.Context;

import com.sumian.sleepdoctor.sleepRecord.bean.SleepRecordSummary;
import com.sumian.sleepdoctor.sleepRecord.view.calendar.calendarViewWrapper.CalendarViewWrapper;
import com.sumian.sleepdoctor.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/2 10:55
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class SleepCalendarViewWrapper extends CalendarViewWrapper {
    private List<SleepRecordSummary> mSleepRecordSummaries = new ArrayList<>();
    private List<Long> mHasSleepRecordDays = new ArrayList<>();
    private List<Long> mHasDoctorEvaluationDays = new ArrayList<>();
    private long mSelectDayTime;
    private long mTodayTime;
    private long mMonthTime;

    public SleepCalendarViewWrapper(Context context) {
        super(context);
    }

    @Override
    protected void init() {
        super.init();
    }

    public void setSleepRecordSummaries(List<SleepRecordSummary> sleepRecordSummaries) {
        mSleepRecordSummaries = sleepRecordSummaries;
        if (sleepRecordSummaries == null) {
            return;
        }
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
        } else if (timeInMillis == mSelectDayTime) {
            dayType = SleepDayType.TYPE_SELECTED_DAY;
        }
        return dayType;
    }

    public void setSleepRecordSummaries(Map<String, List<SleepRecordSummary>> map) {
        if (map == null) {
            return;
        }
        for (Map.Entry<String, List<SleepRecordSummary>> entry : map.entrySet()) {
            mSleepRecordSummaries.addAll(entry.getValue());
        }
        mAdapter.notifyDataSetChanged();
    }
}
