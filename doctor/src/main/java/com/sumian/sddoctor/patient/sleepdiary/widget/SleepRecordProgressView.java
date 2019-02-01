package com.sumian.sddoctor.patient.sleepdiary.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sumian.sddoctor.R;
import com.sumian.sddoctor.service.report.widget.progress.ColorfulProgressView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

    private ColorfulProgressView progressView;
    private TextView tvPercent;
    private TextView tvPercentMark;

    private int mColor0 = getResources().getColor(R.color.b3_color);
    private int mColor1 = getResources().getColor(R.color.b4_color);
    private int mColor2 = getResources().getColor(R.color.t4_color);

    public SleepRecordProgressView(@NonNull Context context) {
        this(context, null);
    }

    public SleepRecordProgressView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View inflate = inflate(context, R.layout.lay_report_progress_view, this);
        progressView = inflate.findViewById(R.id.progress_view);
        tvPercent = inflate.findViewById(R.id.tv_percent);
        tvPercentMark = inflate.findViewById(R.id.tv_percent_mark);
        TextView tvCenterMessage = inflate.findViewById(R.id.tv_center_message);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SleepRecordProgressView);
        boolean showCenterMessage = typedArray.getBoolean(R.styleable.SleepRecordProgressView_srpv_show_center_message, true);
        String centerMessage = typedArray.getString(R.styleable.SleepRecordProgressView_srpv_center_message);
        float ringWidth = typedArray.getDimension(R.styleable.SleepRecordProgressView_srpv_ring_width, getResources().getDimension(R.dimen.space_10));
        typedArray.recycle();

        tvCenterMessage.setVisibility(showCenterMessage ? VISIBLE : GONE);
        tvCenterMessage.setText(centerMessage);
        progressView.setRingWidth(ringWidth);
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
                return mColor0;
            case 1:
                return mColor1;
            case 2:
                return mColor2;
            default:
                return mColor0;
        }
    }

    public void setProgressColors(int[][] colors) {
        progressView.setProgressColors(colors);
    }

    public void setTextColors(int[] colors) {
        mColor0 = colors[0];
        mColor1 = colors[1];
        mColor2 = colors[2];
    }
}
