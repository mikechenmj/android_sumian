package com.sumian.sleepdoctor.widget.calendar;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/29 15:58
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class SleepCalendarAdapter extends CalendarAdapter {

    @NonNull
    @Override
    public CalendarVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return SleepCalendarVH.create(parent);
    }
}
