package com.sumian.hw.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.sumian.sd.R;
import com.sumian.hw.common.util.TimeUtil;
import com.sumian.hw.widget.base.BaseBlueCardView;

/**
 * Created by sm
 * on 2018/3/8.
 * desc:
 */

public class ReportSleepDurationView extends BaseBlueCardView {

    TextView mTvSleepTodayDuration;
    SleepStateDurationView mLightSleepDurationView;
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

    @Override
    protected void initView(View inflate) {
        super.initView(inflate);

        mTvSleepTodayDuration = inflate.findViewById(R.id.tv_sleep_today_duration);
        mLightSleepDurationView = inflate.findViewById(R.id.light_sleep_duration_view);
        mDeepSleepDurationView = inflate.findViewById(R.id.deep_sleep_duration_view);
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
