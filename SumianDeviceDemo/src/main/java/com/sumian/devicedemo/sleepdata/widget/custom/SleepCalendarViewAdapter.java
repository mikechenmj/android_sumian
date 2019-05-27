package com.sumian.devicedemo.sleepdata.widget.custom;

import android.view.ViewGroup;

import com.sumian.devicedemo.sleepdata.widget.calendarView.CalendarViewAdapter;
import com.sumian.devicedemo.sleepdata.widget.calendarView.CalendarViewVH;

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
public class SleepCalendarViewAdapter extends CalendarViewAdapter {

    @NonNull
    @Override
    public CalendarViewVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return SleepCalendarViewVH.Companion.create(parent);
    }
}
