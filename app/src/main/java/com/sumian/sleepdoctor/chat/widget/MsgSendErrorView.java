package com.sumian.sleepdoctor.chat.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.sumian.sleepdoctor.R;

import butterknife.ButterKnife;

/**
 * Created by sm
 * on 2018/1/29.
 * desc:
 */

public class MsgSendErrorView extends FrameLayout {


    public MsgSendErrorView(@NonNull Context context) {
        this(context, null);
    }

    public MsgSendErrorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MsgSendErrorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        ButterKnife.bind(inflate(context, R.layout.lay_send_msg_error, this));
    }
}
