package com.sumian.sleepdoctor.improve.record.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.sumian.sleepdoctor.R;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/4 9:23
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class ReportProgressView extends FrameLayout {
    public ReportProgressView(@NonNull Context context) {
        this(context, null);
    }

    public ReportProgressView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.lay_report_progress_view, this);
    }
}
