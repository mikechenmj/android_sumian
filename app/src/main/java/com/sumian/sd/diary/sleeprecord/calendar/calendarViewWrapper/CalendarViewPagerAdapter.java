package com.sumian.sd.diary.sleeprecord.calendar.calendarViewWrapper;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.sumian.sd.diary.sleeprecord.calendar.calendarView.CalendarView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/12 10:17
 * desc   :
 * version: 1.0
 */
public class CalendarViewPagerAdapter extends PagerAdapter {

    private CalendarView.OnDateClickListener mOnDateClickListener;
    private List<Long> mMonthTimes = new ArrayList<>();
    private CalendarView.DayTypeProvider mDayTypeProvider;

    public CalendarViewPagerAdapter(CalendarView.DayTypeProvider dayTypeProvider) {
        mDayTypeProvider = dayTypeProvider;
    }

    @Override
    public int getCount() {
        return mMonthTimes == null ? 0 : mMonthTimes.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        long time = (long) ((View) object).getTag();
        return getPositionByTime(time);
    }

    private int getPositionByTime(long time) {
        for (int i = 0; i < mMonthTimes.size(); i++) {
            if (mMonthTimes.get(i).equals(time)) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        CalendarView calendarView = new CalendarView(container.getContext());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        calendarView.setLayoutParams(layoutParams);
        initCalendarView(position, calendarView);
        container.addView(calendarView);
        return calendarView;
    }

    private void initCalendarView(int position, CalendarView calendarView) {
        calendarView.setOnDateClickListener(mOnDateClickListener);
        calendarView.setDayTypeProvider(mDayTypeProvider);
        calendarView.setMonthTime(mMonthTimes.get(position));
        calendarView.setTag(mMonthTimes.get(position));
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    public void setOnDateClickListener(CalendarView.OnDateClickListener onDateClickListener) {
        mOnDateClickListener = onDateClickListener;
    }

    public List<Long> getMonthTimes() {
        return mMonthTimes;
    }

    public void setMonthTimes(List<Long> monthTimes) {
        Collections.sort(monthTimes);
        mMonthTimes = monthTimes;
        notifyDataSetChanged();
    }

    public void addMonthTimes(List<Long> monthTimes) {
        Collections.sort(monthTimes);
        mMonthTimes.addAll(0, monthTimes);
        notifyDataSetChanged();
    }

    private void sortAndReverseTime(List<Long> monthTimes) {
        Collections.sort(monthTimes);
        Collections.reverse(monthTimes);
    }
}
