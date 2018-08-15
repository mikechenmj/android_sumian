package com.sumian.hw.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.sumian.sd.R;

/**
 * Created by jzz
 * on 2017/12/27.
 * desc:
 */

public class MsgEmptyView extends LinearLayout {

    public MsgEmptyView(Context context) {
        this(context, null);
    }

    public MsgEmptyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MsgEmptyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.hw_lay_empty_msg, this, true);
        setVisibility(GONE);
    }

    public void hide() {
        setVisibility(GONE);
    }

    public void show() {
        setVisibility(VISIBLE);
    }
}
