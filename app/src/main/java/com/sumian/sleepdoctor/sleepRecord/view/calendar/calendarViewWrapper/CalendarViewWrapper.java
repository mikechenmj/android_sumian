package com.sumian.sleepdoctor.sleepRecord.view.calendar.calendarViewWrapper;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.sleepRecord.view.calendar.calendarView.CalendarView;
import com.sumian.sleepdoctor.utils.TimeUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/29 17:13
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class CalendarViewWrapper extends LinearLayout implements RecyclerViewPager.OnPageChangedListener, CalendarView.DayTypeProvider {

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
    @BindView(R.id.v_bg)
    View vBg;
    protected CalendarWrapperAdapter mAdapter;
    private int mCurrentPosition;
    private CalendarView.OnDateClickListener mOnDateClickListener;
    private List<Long> mMonthTimeList;

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
        mAdapter = new CalendarWrapperAdapter(this);
        mRecyclerViewPager.setAdapter(mAdapter);
        mRecyclerViewPager.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true));
        mRecyclerViewPager.addOnPageChangedListener(this);
        updateTvMonth(System.currentTimeMillis());
    }

    private void updateTvMonth(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM", Locale.getDefault());
        String format = simpleDateFormat.format(new Date(time));
        tvMonth.setText(format);
    }

    public void setOnDateClickListener(CalendarView.OnDateClickListener listener) {
        mOnDateClickListener = listener;
        mAdapter.setOnDateClickListener(mOnDateClickListener);
    }

    public void scrollToTime(long time, boolean smooth) {
        if (mMonthTimeList == null) {
            return;
        }
        for (int i = 0; i < mMonthTimeList.size(); i++) {
            long monthTime = mMonthTimeList.get(i);
            if (TimeUtil.isInTheSameMonth(time, monthTime)) {
                if (smooth) {
                    mRecyclerViewPager.smoothScrollToPosition(i);
                } else {
                    mRecyclerViewPager.scrollToPosition(i);
                }
                return;
            }
        }
    }

    @Override
    public void OnPageChanged(int oldPosition, int newPosition) {
        mCurrentPosition = newPosition;
        updateTvMonth(mMonthTimeList.get(newPosition));
        ivRight.setClickable(newPosition != 0);
        ivLeft.setClickable(newPosition != mMonthTimeList.size() - 1);
    }

    @OnClick({R.id.iv_left, R.id.iv_right, R.id.tv_go_to_today})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                mRecyclerViewPager.smoothScrollToPosition(mCurrentPosition + 1);
                break;
            case R.id.iv_right:
                mRecyclerViewPager.smoothScrollToPosition(mCurrentPosition - 1);
                break;
            case R.id.tv_go_to_today:
                mOnDateClickListener.onDateClick(System.currentTimeMillis());
                break;
        }
    }

    public void setMonthTimes(List<Long> monthTimeList) {
        mMonthTimeList = monthTimeList;
        mAdapter.setMonthTimes(monthTimeList);
    }

    public void setOnBgClickListener(OnClickListener listener) {
        vBg.setOnClickListener(listener);
    }

    @Override
    public int getDayTypeByTime(long timeInMillis) {
        return 0;
    }
}
