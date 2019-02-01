package com.sumian.sddoctor.service.report.widget.calendar.calendarViewWrapper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sumian.sddoctor.R;
import com.sumian.sddoctor.service.report.widget.calendar.calendarView.CalendarView;

import androidx.recyclerview.widget.RecyclerView;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/29 20:30
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class CalendarWrapperVH extends RecyclerView.ViewHolder {
    CalendarView mCalendarView;

    private CalendarWrapperVH(View itemView) {
        super(itemView);
        mCalendarView = itemView.findViewById(R.id.cv);
    }

    public static CalendarWrapperVH create(ViewGroup parent) {
        Context context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.item_view_calendar_wrapper, parent, false);
        return new CalendarWrapperVH(inflate);
    }
}
