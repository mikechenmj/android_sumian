package com.sumian.hw.improve.widget.report;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.qmuiteam.qmui.util.QMUISpanHelper;
import com.sumian.sd.R;
import com.sumian.hw.common.util.TimeUtil;

/**
 * Created by sm
 * on 2018/3/14.
 * desc:
 */

public class SleepAvgAndCompareView extends ConstraintLayout {

    TextView mTvLabel;
    TextView mTvDuration;
    TextView mTvCompareLabel;
    TextView mTvCompareDuration;
    View mDivider;

    private String mLabel;
    private Drawable mLabelDrawable;
    private boolean mIsGoneDivider;

    public SleepAvgAndCompareView(@NonNull Context context) {
        this(context, null);
    }

    public SleepAvgAndCompareView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SleepAvgAndCompareView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.SleepAvgAndCompareView, defStyleAttr, 0);

        this.mLabel = attributes.getString(R.styleable.SleepAvgAndCompareView_label_text);
        this.mLabelDrawable = attributes.getDrawable(R.styleable.SleepAvgAndCompareView_label_icon);
        this.mIsGoneDivider = attributes.getBoolean(R.styleable.SleepAvgAndCompareView_is_gone_divider, false);
        attributes.recycle();
        initView(context);
    }

    private void initView(Context context) {
        View inflate = inflate(context, R.layout.hw_lay_sleep_week_count_view, this);
        mTvLabel = inflate.findViewById(R.id.tv_label);
        mTvDuration = inflate.findViewById(R.id.tv_duration);
        mTvCompareLabel = inflate.findViewById(R.id.tv_compare_label);
        mTvCompareDuration = inflate.findViewById(R.id.tv_compare_duration);
        mDivider = inflate.findViewById(R.id.v_divider);

        CharSequence charSequence = QMUISpanHelper.generateSideIconText(true, getResources().getDimensionPixelSize(R.dimen.space_12), mLabel, mLabelDrawable);
        mTvLabel.setText(charSequence);
        mDivider.setVisibility(mIsGoneDivider ? GONE : VISIBLE);
    }

    public SleepAvgAndCompareView setAvgDuration(Integer duration) {
        CharSequence formatDuration = TimeUtil.formatSleepDurationText(getContext(), duration);
        mTvDuration.setText(formatDuration);
        return this;
    }

    public void setCompareDuration(Integer duration) {
        CharSequence formatDuration = TimeUtil.formatSleepDurationText(getContext(), duration);
        mTvCompareDuration.setText(formatDuration);
        if (duration == null || duration == 0) {
            return;
        }

        Drawable leftDrawable = getResources().getDrawable(duration > 0 ? R.mipmap.ic_report_up : R.mipmap.ic_report_down);

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(formatDuration);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(duration > 0 ? R.color.bt_hole_color : R.color.warn_color)), 0, formatDuration.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        CharSequence charSequence = QMUISpanHelper.generateSideIconText(true, getResources().getDimensionPixelSize(R.dimen.space_4), spannableStringBuilder, leftDrawable);
        mTvCompareDuration.setText(charSequence);
    }

}
