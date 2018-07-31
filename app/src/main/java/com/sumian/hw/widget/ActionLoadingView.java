package com.sumian.hw.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.sumian.sleepdoctor.R;

/**
 * Created by jzz
 * on 2017/12/16.
 * desc:
 */

public class ActionLoadingView extends FrameLayout implements View.OnClickListener {


    public ActionLoadingView(@NonNull Context context) {
        this(context, null);
    }

    public ActionLoadingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActionLoadingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.hw_lay_action_loading, this, true);
        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

    }
}
