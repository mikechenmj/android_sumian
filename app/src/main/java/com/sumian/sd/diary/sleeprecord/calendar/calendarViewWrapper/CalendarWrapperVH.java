package com.sumian.sd.diary.sleeprecord.calendar.calendarViewWrapper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sumian.sd.R;
import com.sumian.sd.diary.sleeprecord.calendar.calendarView.CalendarView;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    @BindView(R.id.cv)
    CalendarView mCalendarView;

    private CalendarWrapperVH(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public static CalendarWrapperVH create(ViewGroup parent) {
        Context context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.item_view_calendar_wrapper, parent, false);
        return new CalendarWrapperVH(inflate);
    }
}
