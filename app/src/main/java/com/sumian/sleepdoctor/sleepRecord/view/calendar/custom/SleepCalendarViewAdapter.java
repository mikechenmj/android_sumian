package com.sumian.sleepdoctor.sleepRecord.view.calendar.custom;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.sumian.sleepdoctor.sleepRecord.view.calendar.calendarView.CalendarViewVH;
import com.sumian.sleepdoctor.sleepRecord.view.calendar.calendarView.CalendarViewAdapter;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/29 15:58
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class SleepCalendarViewAdapter extends CalendarViewAdapter {

    @NonNull
    @Override
    public CalendarViewVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return SleepCalendarViewVH.create(parent);
    }
}
