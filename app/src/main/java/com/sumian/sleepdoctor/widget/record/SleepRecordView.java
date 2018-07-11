package com.sumian.sleepdoctor.widget.record;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.sumian.sleepdoctor.R;

import butterknife.ButterKnife;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/3 20:19
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class SleepRecordView extends LinearLayout {
    public SleepRecordView(Context context) {
        this(context, null);
    }

    public SleepRecordView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.lay_sleep_record_view, this);
        ButterKnife.bind(this);
    }
}
