package com.sumian.hw.report.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.qmuiteam.qmui.util.QMUISpanHelper;
import com.sumian.hw.common.util.TextUtil;
import com.sumian.hw.common.util.TimeUtil;
import com.sumian.hw.report.widget.text.CountSleepDurationTextView;
import com.sumian.sd.R;

/**
 * Created by sm
 * on 2018/3/14.
 * desc:
 */

public class SleepAvgAndCompareView extends ConstraintLayout {

    private CountSleepDurationTextView mTvDuration;
    private TextView mTvCompareDuration;

    private String mLabel;
    private Drawable mLabelDrawable;
    private boolean mIsGoneDivider;
    private TextView mTvLabel;

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
        //TextView tvCompareLabel = inflate.findViewById(R.id.tv_compare_label);
        mTvCompareDuration = inflate.findViewById(R.id.tv_compare_duration);
        View divider = inflate.findViewById(R.id.v_divider);

        setDrawableLabel(mLabelDrawable);
        divider.setVisibility(mIsGoneDivider ? GONE : VISIBLE);
    }

    public void setDrawableLabel(Drawable drawable) {
        CharSequence charSequence = QMUISpanHelper.generateSideIconText(true, getResources().getDimensionPixelSize(R.dimen.space_12), mLabel, drawable);
        mTvLabel.setText(charSequence);
    }

    public SleepAvgAndCompareView setAvgDuration(Integer duration) {
        // CharSequence formatDuration = TimeUtil.formatSleepDurationText(getContext(), duration);
        mTvDuration.setDuration(duration);
        return this;
    }

    public void setCompareDuration(Integer duration) {
        if (duration == null) {
            mTvCompareDuration.setText(R.string.none_sleep_status_data);
        } else if (duration == 0) {
//            Drawable defaultDrawable = getResources().getDrawable(R.drawable.bg_text_t5);
//            defaultDrawable.setTint(mTvCompareDuration.getCurrentTextColor());
//            CharSequence charSequence = QMUISpanHelper.generateSideIconText(false, 0, " ", defaultDrawable);
//            CharSequence concat = TextUtils.concat(charSequence, " ");
//            mTvCompareDuration.setText(concat);

            int numberSize = getResources().getDimensionPixelSize(R.dimen.font_22);
            int unitSize = getResources().getDimensionPixelSize(R.dimen.font_12);

            CharSequence formatDuration = TextUtils.concat(TextUtil.getSpannableString(0, numberSize), TextUtil.getSpannableString("分钟", unitSize));
            mTvCompareDuration.setText(formatDuration);
        } else {

            CharSequence formatDuration = TimeUtil.formatSleepDurationText(getContext(), duration);
            mTvCompareDuration.setText(formatDuration);

            Drawable leftDrawable = getResources().getDrawable(duration > 0 ? R.mipmap.ic_report_up : R.mipmap.ic_report_down);

            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(formatDuration);
            spannableStringBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(duration > 0 ? R.color.bt_hole_color : R.color.warn_color)), 0, formatDuration.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

            CharSequence charSequence = QMUISpanHelper.generateSideIconText(true, getResources().getDimensionPixelSize(R.dimen.space_4), spannableStringBuilder, leftDrawable);
            mTvCompareDuration.setText(charSequence);
        }

    }

}
