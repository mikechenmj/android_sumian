package com.sumian.sleepdoctor.sleepRecord.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/4 9:23
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class SleepRecordProgressView extends FrameLayout {
    @BindView(R.id.progress_view)
    ColorfulProgressView progressView;
    @BindView(R.id.tv_percent)
    TextView tvPercent;
    @BindView(R.id.tv_percent_mark)
    TextView tvPercentMark;

    public SleepRecordProgressView(@NonNull Context context) {
        this(context, null);
    }

    public SleepRecordProgressView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.lay_report_progress_view, this);
        ButterKnife.bind(this);
    }

    public void setProgress(int progress) {
        progressView.setProgress(progress);
        int level = progressView.getProgressLevelByPercent(progress);
        tvPercent.setText(String.valueOf(progress));
        int color = getColor(level);
        tvPercent.setTextColor(color);
        tvPercentMark.setTextColor(color);
    }

    public int getColor(int level) {
        switch (level) {
            case 0:
                return getResources().getColor(R.color.b3_color);
            case 1:
                return getResources().getColor(R.color.b4_color);
            case 2:
                return getResources().getColor(R.color.t4_color);
            default:
                return getResources().getColor(R.color.b3_color);
        }
    }
}
