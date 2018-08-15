package com.sumian.hw.improve.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumian.sd.R;
import com.sumian.hw.common.util.TimeUtil;

/**
 * Created by sm
 * on 2018/3/8.
 * desc:睡眠报告中,睡眠时长统计容器
 */

public class SleepStateDurationView extends LinearLayout {

    TextView mTvLabel;
    SleepPercentTextView mTvSleepDurationPercent;
    TextView mTvSleepDurationCount;

    public SleepStateDurationView(Context context) {
        this(context, null);
    }

    public SleepStateDurationView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SleepStateDurationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setGravity(Gravity.CENTER);
        setOrientation(VERTICAL);
        initView(context);
    }

    private void initView(Context context) {
        View inflate = inflate(context, R.layout.hw_lay_sleep_state_duration_view, this);
        mTvLabel = inflate.findViewById(R.id.tv_label);
        mTvSleepDurationPercent = inflate.findViewById(R.id.tv_sleep_duration_percent);
        mTvSleepDurationCount = inflate.findViewById(R.id.tv_sleep_duration_count);
    }

    public void setData(String label, int duration, int percent) {
        mTvLabel.setText(label);
        mTvSleepDurationCount.setText(TimeUtil.formatSleepDurationText(getContext(), duration));
        mTvSleepDurationPercent.setPercent(percent);
    }
}
