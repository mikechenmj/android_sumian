package com.sumian.hw.widget.refresh;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

import com.sumian.sleepdoctor.R;

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
        init();
    }

    private void init() {

        setProgressBackgroundColorSchemeResource(R.color.refresh_bg_color);
        setColorSchemeResources(R.color.white);
    }
}
