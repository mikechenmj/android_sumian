package com.sumian.sleepdoctor.widget.calendar;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.sumian.sleepdoctor.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/29 15:53
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class CalendarView extends FrameLayout {
    public static final int DAY_TYPE_NORMAL = 0;
    public static final int DAY_TYPE_EMPHASIZE_1 = 1;
    public static final int DAY_TYPE_EMPHASIZE_2 = 2;
    public static final int DAY_TYPE_EMPHASIZE_3 = 3;
    public static final int DAY_TYPE_EMPHASIZE_4 = 4;
    public static final int DAY_TYPE_EMPHASIZE_5 = 5;
    public static final int DAY_TYPE_DE_EMPHASIZE_1 = -1;
    private CalendarAdapter mAdapter;

    @IntDef({
            DAY_TYPE_NORMAL,
            DAY_TYPE_EMPHASIZE_1,
            DAY_TYPE_EMPHASIZE_2,
            DAY_TYPE_EMPHASIZE_3,
            DAY_TYPE_EMPHASIZE_4,
            DAY_TYPE_EMPHASIZE_5,
            DAY_TYPE_DE_EMPHASIZE_1,
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface DayType {
    }

    @BindView(R.id.rv)
    RecyclerView mRecyclerView;

    public CalendarView(@NonNull Context context) {
        this(context, null);
    }

    public CalendarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View inflate = inflate(context, R.layout.view_calendar, this);
        ButterKnife.bind(this, inflate);
        mAdapter = new SleepCalendarAdapter();
        mAdapter.setMonthTime(System.currentTimeMillis());
        mAdapter.setEmphasizeDays(DAY_TYPE_EMPHASIZE_1, Arrays.asList(1, 2, 3));
        mAdapter.setEmphasizeDays(DAY_TYPE_EMPHASIZE_2, Arrays.asList(4, 5, 6));
        mAdapter.setEmphasizeDays(DAY_TYPE_EMPHASIZE_3, Arrays.asList(7, 8, 9));
        mAdapter.setEmphasizeDays(DAY_TYPE_EMPHASIZE_4, Arrays.asList(10, 11, 12));
        mRecyclerView.setLayoutManager(new GridLayoutManager(context, 7));
        mRecyclerView.setAdapter(mAdapter);
    }

    public void setEmphasizeDays(@CalendarView.DayType int dayType, List<Integer> days) {
        mAdapter.setEmphasizeDays(dayType, days);
    }

    public void setOnDateClickListener(CalendarAdapter.OnDateClickListener onDateClickListener) {
        mAdapter.setOnDateClickListener(onDateClickListener);
    }

    public void setMonthTime(long monthTime) {
        mAdapter.setMonthTime(monthTime);
    }
}
