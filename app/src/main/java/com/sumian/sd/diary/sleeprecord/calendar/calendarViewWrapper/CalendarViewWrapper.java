package com.sumian.sd.diary.sleeprecord.calendar.calendarViewWrapper;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;
import com.sumian.sd.R;
import com.sumian.sd.diary.sleeprecord.calendar.calendarView.CalendarView;
import com.sumian.sd.utils.TimeUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/29 17:13
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class CalendarViewWrapper extends LinearLayout implements CalendarView.DayTypeProvider, ViewPager.OnPageChangeListener {


    @BindView(R.id.tv_month)
    TextView tvMonth;
    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.tv_go_to_today)
    TextView tvGoToToday;
    @BindView(R.id.v_bg)
    View vBg;
    @BindView(R.id.calender_view_pager)
    ViewPager mViewPager;
    protected int mCurrentPosition;
    protected CalendarView.OnDateClickListener mOnDateClickListener;
    protected CalendarViewPagerAdapter mPagerAdapter;


    public CalendarViewWrapper(Context context) {
        this(context, null);
    }

    public CalendarViewWrapper(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View inflate = inflate(context, R.layout.view_calendar_wrapper, this);
        ButterKnife.bind(this, inflate);
        init();
    }

    protected void init() {
        mPagerAdapter = new CalendarViewPagerAdapter(this);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);
    }

    private void updateTvMonth(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM", Locale.getDefault());
        String format = simpleDateFormat.format(new Date(time));
        tvMonth.setText(format);
    }

    public void setOnDateClickListener(CalendarView.OnDateClickListener listener) {
        mOnDateClickListener = listener;
        mPagerAdapter.setOnDateClickListener(mOnDateClickListener);
    }

    public void scrollToTime(long time, boolean smooth) {
        updateTvMonth(time);
        if (getMonthTimes() == null) {
            return;
        }
        for (int i = 0; i < getMonthTimes().size(); i++) {
            long monthTime = getMonthTimes().get(i);
            if (TimeUtil.isInTheSameMonth(time, monthTime)) {
                mViewPager.setCurrentItem(i, smooth);
                return;
            }
        }
    }

    @OnClick({R.id.iv_left, R.id.iv_right, R.id.tv_go_to_today})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                mViewPager.setCurrentItem(mCurrentPosition - 1, true);
                break;
            case R.id.iv_right:
                mViewPager.setCurrentItem(mCurrentPosition + 1, true);
                break;
            case R.id.tv_go_to_today:
                mOnDateClickListener.onDateClick(TimeUtil.getStartTimeOfTheDay(System.currentTimeMillis()));
                break;
            default:
                break;
        }
    }

    public void addMonthTimes(List<Long> monthTimes) {
        mPagerAdapter.addMonthTimes(monthTimes);
    }

    public List<Long> getMonthTimes() {
        if (mPagerAdapter == null) {
            return new ArrayList<>();
        }
        return mPagerAdapter.getMonthTimes();
    }

    public void setMonthTimes(List<Long> monthTimeList) {
        mPagerAdapter.setMonthTimes(monthTimeList);
        if (monthTimeList.size() > 0) {
            mViewPager.setCurrentItem(monthTimeList.size() - 1);
        }
    }

    public void setOnBgClickListener(OnClickListener listener) {
        vBg.setOnClickListener(listener);
    }

    @Override
    public int getDayTypeByTime(long timeInMillis) {
        return 0;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPosition = position;
        LogUtils.d(position);
        updateTvMonth(getMonthTimes().get(position));
        ivLeft.setVisibility(position != 0 ? VISIBLE : GONE);
        ivRight.setVisibility(position != getMonthTimes().size() - 1 ? VISIBLE : GONE);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
