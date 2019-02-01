package com.sumian.sddoctor.service.report.widget.calendar.calendarView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.sumian.sddoctor.R;
import com.sumian.sddoctor.service.report.widget.calendar.custom.SleepCalendarViewAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/29 15:53
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class CalendarView extends FrameLayout {
    private CalendarViewAdapter mAdapter;

    public CalendarView(@NonNull Context context) {
        this(context, null);
    }

    public CalendarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View inflate = inflate(context, R.layout.view_calendar, this);
        RecyclerView recyclerView = inflate.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 7));
        mAdapter = new SleepCalendarViewAdapter();
        recyclerView.setAdapter(mAdapter);
    }

    public void setOnDateClickListener(OnDateClickListener onDateClickListener) {
        mAdapter.setOnDateClickListener(onDateClickListener);
    }

    public void setDayTypeProvider(DayTypeProvider provider) {
        mAdapter.setDayTypeProvider(provider);
    }

    public void setMonthTime(long monthTimeInMillis) {
        mAdapter.setMonthTime(monthTimeInMillis);
    }

    public interface DayTypeProvider {
        int getDayTypeByTime(long timeInMillis);
    }

    public interface OnDateClickListener {
        void onDateClick(long time);
    }
}
