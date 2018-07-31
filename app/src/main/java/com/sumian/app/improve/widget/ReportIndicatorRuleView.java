package com.sumian.app.improve.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;

import com.sumian.app.R;

/**
 * Created by sm
 * on 2018/3/6.
 * desc:
 */

public class ReportIndicatorRuleView extends LinearLayout {

    public ReportIndicatorRuleView(Context context) {
        this(context, null);
    }

    public ReportIndicatorRuleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReportIndicatorRuleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        setGravity(Gravity.CENTER);
        setOrientation(HORIZONTAL);
        inflate(context, R.layout.hw_lay_report_sleep_rule, this);
    }
}
