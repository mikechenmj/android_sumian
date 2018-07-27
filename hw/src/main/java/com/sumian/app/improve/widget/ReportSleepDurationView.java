package com.sumian.app.improve.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.sumian.app.R;
import com.sumian.app.common.util.TimeUtil;
import com.sumian.app.improve.widget.base.BaseBlueCardView;

import butterknife.BindView;

/**
 * Created by sm
 * on 2018/3/8.
 * desc:
 */

public class ReportSleepDurationView extends BaseBlueCardView {

    @BindView(R.id.tv_sleep_today_duration)
    TextView mTvSleepTodayDuration;
    @BindView(R.id.light_sleep_duration_view)
    SleepStateDurationView mLightSleepDurationView;
    @BindView(R.id.deep_sleep_duration_view)
    SleepStateDurationView mDeepSleepDurationView;

    public ReportSleepDurationView(@NonNull Context context) {
        this(context, null);
    }

    public ReportSleepDurationView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReportSleepDurationView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.hw_lay_sleep_duration_view;
    }

    public void setSleepTodayDuration(int sleepTodayDuration) {
        CharSequence charSequence = TimeUtil.formatSleepDurationText(getContext(), sleepTodayDuration);
        mTvSleepTodayDuration.setText(charSequence);
    }

    public void setDeepSleepData(int deepSleepDuration, int deepSleepDurationPercent) {
        mDeepSleepDurationView.setData("深睡时长", deepSleepDuration, deepSleepDurationPercent);
    }

    public void setLightSleepData(int lightSleepDuration, int lightSleepDurationPercent) {
        mLightSleepDurationView.setData("浅睡时长", lightSleepDuration, lightSleepDurationPercent);
    }

}
