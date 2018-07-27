package com.sumian.app.improve.widget.report;

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
import com.sumian.app.R;
import com.sumian.app.common.util.TimeUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sm
 * on 2018/3/14.
 * desc:
 */

public class SleepAvgAndCompareView extends ConstraintLayout {

    @BindView(R.id.tv_label)
    TextView mTvLabel;
    @BindView(R.id.tv_duration)
    TextView mTvDuration;

    @BindView(R.id.tv_compare_label)
    TextView mTvCompareLabel;
    @BindView(R.id.tv_compare_duration)
    TextView mTvCompareDuration;

    @BindView(R.id.v_divider)
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
        ButterKnife.bind(inflate(context, R.layout.hw_lay_sleep_week_count_view, this));
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
        if (duration == null || duration == 0) return;

        Drawable leftDrawable = getResources().getDrawable(duration > 0 ? R.mipmap.ic_report_up : R.mipmap.ic_report_down);

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(formatDuration);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(duration > 0 ? R.color.bt_hole_color : R.color.warn_color)), 0, formatDuration.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        CharSequence charSequence = QMUISpanHelper.generateSideIconText(true, getResources().getDimensionPixelSize(R.dimen.space_4), spannableStringBuilder, leftDrawable);
        mTvCompareDuration.setText(charSequence);
    }

}
