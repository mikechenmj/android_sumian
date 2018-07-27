package com.sumian.app.improve.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumian.app.R;
import com.sumian.app.common.util.TimeUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sm
 * on 2018/3/8.
 * desc:睡眠报告中,睡眠时长统计容器
 */

public class SleepStateDurationView extends LinearLayout {

    @BindView(R.id.tv_label)
    TextView mTvLabel;

    @BindView(R.id.tv_sleep_duration_percent)
    SleepPercentTextView mTvSleepDurationPercent;

    @BindView(R.id.tv_sleep_duration_count)
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
        ButterKnife.bind(inflate(context, R.layout.hw_lay_sleep_state_duration_view, this));
    }

    public void setData(String label, int duration, int percent) {
        mTvLabel.setText(label);
        mTvSleepDurationCount.setText(TimeUtil.formatSleepDurationText(getContext(),duration));
        mTvSleepDurationPercent.setPercent(percent);
    }
}
