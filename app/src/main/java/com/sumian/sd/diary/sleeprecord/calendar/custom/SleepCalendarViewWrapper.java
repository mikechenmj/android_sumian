package com.sumian.sd.diary.sleeprecord.calendar.custom;

import android.content.Context;
import android.text.format.DateUtils;

import com.sumian.sd.R;
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
    public static final int              PRELOAD_THRESHOLD = 3;
    private             Set<Long>        mHasRecordDays = new HashSet<>();
    private             long             mSelectDayTime;
    private             long             mTodayTime;
    private             LoadMoreListener mLoadMoreListener;
    private             boolean          mIsWeekMode = false;
    private             long             mWeekStartDayTime;
    private             long             mWeekEndDayTime;
    private             long             mPreviewDays;

    public SleepCalendarViewWrapper(Context context) {
        super(context);
    }

    @Override
    protected void init() {
        super.init();
    }

    public void setSelectDayTime(long selectDayTime) {
        mSelectDayTime = TimeUtil.getDayStartTime(selectDayTime);
        mWeekStartDayTime = TimeUtil.getWeekStartDayTime(selectDayTime);
        mWeekEndDayTime = TimeUtil.getWeekEndDayTime(selectDayTime);
        scrollToTime(mSelectDayTime, false);
    }

    public void setTodayTime(long todayTime) {
        mTodayTime = TimeUtil.getDayStartTime(todayTime);
    }

    public void setPreviewDays(int previewDays) {
        mPreviewDays = previewDays;
    }

    @Override
    public int getDayTypeByTime(long timeInMillis) {
        int dayType;
        boolean hasData = mHasRecordDays.contains(timeInMillis);
        if (!mIsWeekMode && timeInMillis == mSelectDayTime) {
            dayType = hasData ? SleepDayType.SELECT_HAS_DATA : SleepDayType.SELECT_NO_DATA;
        } else if (timeInMillis > mTodayTime + DateUtils.DAY_IN_MILLIS * mPreviewDays) {
            dayType = SleepDayType.FEATURE;
        } else {
            dayType = hasData ? SleepDayType.HAS_DATA : SleepDayType.NO_DATA;
        }
        return dayType;
    }

    @Override
    public int getSecondDayType(long timeInMillis) {
        if (mIsWeekMode) {
            if (mWeekStartDayTime <= timeInMillis && timeInMillis <= mWeekEndDayTime) {
                if (TimeUtil.isAtStartOfWeek(timeInMillis) || TimeUtil.isAtStartOfMonth(timeInMillis)) {
                    if (TimeUtil.isAtEndOfWeek(timeInMillis) || TimeUtil.isAtEndOfMonth(timeInMillis)) {
                        return SleepCalendarViewVH.Companion.getSECOND_BG_TYPE_START_END();
                    } else {
                        return SleepCalendarViewVH.Companion.getSECOND_BG_TYPE_START();
                    }
                } else if (TimeUtil.isAtEndOfWeek(timeInMillis) || TimeUtil.isAtEndOfMonth(timeInMillis)) {
                    //noinspection ConstantConditions
                    if (TimeUtil.isAtStartOfWeek(timeInMillis) || TimeUtil.isAtStartOfMonth(timeInMillis)) {
                        return SleepCalendarViewVH.Companion.getSECOND_BG_TYPE_START_END();
                    } else {
                        return SleepCalendarViewVH.Companion.getSECOND_BG_TYPE_END();
                    }
                } else {
                    return SleepCalendarViewVH.Companion.getSECOND_BG_TYPE_MIDDLE();
                }
            } else {
                return SleepCalendarViewVH.Companion.getSECOND_BG_TYPE_NONE();
            }
        } else {
            return SleepCalendarViewVH.Companion.getSECOND_BG_TYPE_NONE();
        }
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
        if (isInit) {
            scrollToTime(mSelectDayTime, false);
        }
    }

    public void setIsWeekMode(boolean isWeekMode) {
        mIsWeekMode = isWeekMode;
        mTvGoBack.setText(isWeekMode ? R.string.return_to_this_week : R.string.return_to_today);
    }

    private boolean isInTheWeek() {
        return false;
    }

    private boolean isFirstDayOfTheWeek() {
        return false;
    }

    private boolean isLastDayOfTheWeek() {
        return false;
    }

    private boolean isLastDayOfTheMonth() {
        return false;
    }
}
