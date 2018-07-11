package com.sumian.sleepdoctor.record.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.record.bean.SleepRecord;
import com.sumian.sleepdoctor.utils.TimeUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/31 20:02
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class SimpleSleepRecordView extends LinearLayout {
    @BindView(R.id.title_view_sleep_record)
    TitleView titleViewSleepRecord;
    @BindView(R.id.tv_sleep_duration)
    TextView tvSleepDuration;
    @BindView(R.id.tv_fall_asleep_duration)
    TextView tvFallAsleepDuration;
    @BindView(R.id.ll_sleep_record)
    LinearLayout llSleepRecord;
    @BindView(R.id.progress_view_sleep)
    SleepRecordProgressView progressViewSleep;
    @BindView(R.id.btn_go_record)
    TextView btnGoRecord;
    @BindView(R.id.ll_no_sleep_record)
    LinearLayout llNoSleepRecord;

    public SimpleSleepRecordView(Context context) {
        this(context, null);
    }

    public SimpleSleepRecordView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View inflate = inflate(context, R.layout.view_simple_sleep_record_view, this);
        ButterKnife.bind(this, inflate);
    }

    public void setSleepRecord(SleepRecord sleepRecord) {
        boolean hasRecord = sleepRecord != null;
        llSleepRecord.setVisibility(hasRecord ? VISIBLE : GONE);
        llNoSleepRecord.setVisibility(hasRecord ? GONE : VISIBLE);
        titleViewSleepRecord.setVisibility(VISIBLE);
        boolean showRefill = hasRecord && TextUtils.isEmpty(sleepRecord.getDoctor_evaluation());
        titleViewSleepRecord.setTvMenuVisibility(showRefill ? VISIBLE : GONE);
        if (hasRecord) {
            showSleepRecord(sleepRecord);
        }
    }

    private void showSleepRecord(SleepRecord sleepRecord) {
        tvSleepDuration.setText(TimeUtil.getHourMinuteStringFromSecondInZh(sleepRecord.getSleep_duration()));
        tvFallAsleepDuration.setText(TimeUtil.getHourMinuteStringFromSecondInZh(sleepRecord.getFall_asleep_duration()));
        progressViewSleep.setProgress(sleepRecord.getSleep_efficiency());
    }

    public void setOnClickRightArrowListener(OnClickListener listener) {
        titleViewSleepRecord.setOnRightArrowClickListener(listener);
    }

    public void setOnClickFillSleepRecordBtnListener(OnClickListener listener) {
        btnGoRecord.setOnClickListener(listener);
    }
}
