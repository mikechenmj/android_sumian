package com.sumian.app.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jzz
 * on 2017/12/13.
 * desc:
 */

public class LoadingHeaderView extends LinearLayout {

    ProgressBar mLoading;
    TextView mTvLoading;

    public LoadingHeaderView(Context context) {
        this(context, null);
    }

    public LoadingHeaderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingHeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setVisibility(GONE);
        init(context);
    }

    private void init(Context context) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.hw_lay_syncing_sleep_cha_view, this, true);
        ButterKnife.bind(inflate);
        mLoading = inflate.findViewById(R.id.loading);
        mTvLoading = inflate.findViewById(R.id.tv_loading);
    }


    public void show() {
        post(() -> setVisibility(VISIBLE));
    }

    public void hide() {
        post(() -> setVisibility(GONE));
    }
}
