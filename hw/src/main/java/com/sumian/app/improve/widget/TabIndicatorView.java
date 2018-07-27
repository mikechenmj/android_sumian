package com.sumian.app.improve.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.sumian.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sm
 * on 2018/3/6.
 * desc:
 */

public class TabIndicatorView extends FrameLayout implements TabIndicatorItemView.OnSelectTabCallback {

    private static final String TAG = TabIndicatorView.class.getSimpleName();

    @BindView(R.id.day_tab_indicator_item_view)
    TabIndicatorItemView mDayTabIndicatorItemView;

    @BindView(R.id.week_tab_indicator_item_view)
    TabIndicatorItemView mWeekTabIndicatorItemView;

    @BindView(R.id.calendar_tab_indicator_item_view)
    TabIndicatorItemView mCalendarTabIndicatorItemView;


    private OnSwitchIndicatorCallback mOnSwitchIndicatorCallback;

    public TabIndicatorView(@NonNull Context context) {
        this(context, null);
    }

    public TabIndicatorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabIndicatorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void setOnSwitchIndicatorCallback(OnSwitchIndicatorCallback onSwitchIndicatorCallback) {
        mOnSwitchIndicatorCallback = onSwitchIndicatorCallback;
    }

    private void initView(Context context) {
        ButterKnife.bind(inflate(context, R.layout.hw_lay_tab_indicator_view, this));
        mDayTabIndicatorItemView.setIndicatorText("日");
        mDayTabIndicatorItemView.setOnSelectTabCallback(this);
        mDayTabIndicatorItemView.select();
        mWeekTabIndicatorItemView.setIndicatorText("周");
        mWeekTabIndicatorItemView.setOnSelectTabCallback(this);
        mWeekTabIndicatorItemView.unSelect();
        mCalendarTabIndicatorItemView.setCalendarIcon(R.mipmap.report_calendar);
        mCalendarTabIndicatorItemView.setOnSelectTabCallback(this);
    }

    public void selectTabByPosition(int position) {
        switch (position) {
            case 0:
                onSelect(mDayTabIndicatorItemView, true);
                break;
            case 1:
                onSelect(mWeekTabIndicatorItemView, true);
                break;
        }
    }

    @Override
    public void onSelect(View v, boolean isSelect) {
        int position = 0;
        switch (v.getId()) {
            case R.id.day_tab_indicator_item_view:
                mWeekTabIndicatorItemView.unSelect();
                position = 0;
                if (mOnSwitchIndicatorCallback != null) {
                    mOnSwitchIndicatorCallback.onSwitchIndicator(v, position);
                }
                break;
            case R.id.week_tab_indicator_item_view:
                mDayTabIndicatorItemView.unSelect();
                position = 1;
                if (mOnSwitchIndicatorCallback != null) {
                    mOnSwitchIndicatorCallback.onSwitchIndicator(v, position);
                }
                break;
            case R.id.calendar_tab_indicator_item_view:
                if (mOnSwitchIndicatorCallback != null) {
                    mOnSwitchIndicatorCallback.onShowCalendar(v);
                }
                break;
            default:
                break;
        }
        updateTabsUiBySelectPosition(position);
    }

    public void updateTabsUiBySelectPosition(int position) {
        switch (position) {
            case 0:
                mCalendarTabIndicatorItemView.setVisibility(VISIBLE);
                mDayTabIndicatorItemView.select();
                mWeekTabIndicatorItemView.unSelect();
                break;
            case 1:
                mWeekTabIndicatorItemView.select();
                mDayTabIndicatorItemView.unSelect();
                mCalendarTabIndicatorItemView.setVisibility(INVISIBLE);
                break;
            default:
                break;
        }
    }

    public interface OnSwitchIndicatorCallback {

        void onSwitchIndicator(View v, int position);

        void onShowCalendar(View v);
    }
}
