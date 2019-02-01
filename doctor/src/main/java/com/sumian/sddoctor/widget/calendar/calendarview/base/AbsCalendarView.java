package com.sumian.sddoctor.widget.calendar.calendarview.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.sumian.sddoctor.R;

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
public abstract class AbsCalendarView extends FrameLayout {
    RecyclerView mRecyclerView;
    private AbsCalendarViewAdapter mAdapter;
    private long mTime;

    public AbsCalendarView(@NonNull Context context) {
        this(context, null);
    }

    public AbsCalendarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View inflate = inflate(context, R.layout.view_calendar, this);
        mRecyclerView = inflate.findViewById(R.id.rv);
        mRecyclerView.setLayoutManager(new GridLayoutManager(context, 7));
        mAdapter = createCalendarViewAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    protected abstract AbsCalendarViewAdapter createCalendarViewAdapter();

    public void setOnDateClickListener(OnDateClickListener onDateClickListener) {
        mAdapter.setOnDateClickListener(onDateClickListener);
    }

    public void setDayTypeProvider(DayTypeProvider provider) {
        mAdapter.setDayTypeProvider(provider);
    }

    public void setTime(long time) {
        mTime = time;
        mAdapter.setTime(time);
    }

    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    public interface OnDateClickListener {
        void onDateClick(long time);
    }
}
