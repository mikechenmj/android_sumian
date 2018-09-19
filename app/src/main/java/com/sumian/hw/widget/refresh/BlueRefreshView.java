package com.sumian.hw.widget.refresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

import com.sumian.sd.R;

/**
 * Created by jzz
 * on 2017/10/11.
 * desc:
 */

public class BlueRefreshView extends SwipeRefreshLayout {

    public BlueRefreshView(Context context) {
        this(context, null);
    }

    public BlueRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.BlueRefreshView);
        int progressColor = attributes.getColor(R.styleable.BlueRefreshView_brv_progress_color, getResources().getColor(R.color.n2_color_day));
        int progressBgColor = attributes.getColor(R.styleable.BlueRefreshView_brv_progress_bg_color, getResources().getColor(R.color.b2_color_day));
        attributes.recycle();

        setColorSchemeColors(progressColor);
        setProgressBackgroundColorSchemeColor(progressBgColor);

    }
}
