package com.sumian.sleepdoctor.widget.calendar;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.utils.TimeUtil;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AppSettingsDialogHolderActivity;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/29 17:13
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class CalendarViewWrapper extends LinearLayout {

    @BindView(R.id.tv_month)
    TextView tvMonth;
    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.tv_go_to_today)
    TextView tvGoToToday;
    @BindView(R.id.rvp)
    RecyclerViewPager mRecyclerViewPager;
    private List<Long> mMonths;

    public CalendarViewWrapper(Context context) {
        this(context, null);
    }

    public CalendarViewWrapper(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View inflate = inflate(context, R.layout.view_calendar_wrapper, this);
        ButterKnife.bind(this, inflate);
        init();
    }

    private void init() {
        long l = System.currentTimeMillis();
        mMonths = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            mMonths.add(l - i * DateUtils.DAY_IN_MILLIS * 31);
        }
        CalendarWrapperAdapter adapter = new CalendarWrapperAdapter();
        adapter.setMonths(mMonths);

        mRecyclerViewPager.setAdapter(adapter);
        mRecyclerViewPager.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true));
        mRecyclerViewPager.addOnPageChangedListener((oldPosition, newPosition) -> {
            LogUtils.d("i1: %d, i2: %d", oldPosition, newPosition);
            updateTvMonth(mMonths.get(newPosition));
        });
        updateTvMonth(mMonths.get(0));
    }

    private void updateTvMonth(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM", Locale.getDefault());
        String format = simpleDateFormat.format(new Date(time));
        tvMonth.setText(format);
    }

    public void setOnDateClickListener(CalendarAdapter.OnDateClickListener onDateClickListener) {
    }

    public void setEmphasizeDays(@CalendarView.DayType int dayType, List<Integer> days) {
    }
}
