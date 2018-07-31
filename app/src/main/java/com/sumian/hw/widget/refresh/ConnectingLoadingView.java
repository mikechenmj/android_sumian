package com.sumian.hw.widget.refresh;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.sumian.sleepdoctor.R;

/**
 * Created by sm
 * on 2018/1/27.
 * desc:
 */

public class ConnectingLoadingView extends FrameLayout {


    public ConnectingLoadingView(@NonNull Context context) {
        this(context, null);
    }

    public ConnectingLoadingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ConnectingLoadingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.hw_lay_blue_action_loading, this);
        setVisibility(GONE);
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }
}
