package com.sumian.app.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sumian.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jzz
 * on 2017/12/13.
 * desc:
 */

public class LoadingHeaderView extends LinearLayout {

    @BindView(R.id.loading)
    ProgressBar mLoading;

    @BindView(R.id.tv_loading)
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
        ButterKnife.bind(LayoutInflater.from(context).inflate(R.layout.hw_lay_syncing_sleep_cha_view, this, true));
    }


    public void show() {
        post(() -> setVisibility(VISIBLE));
    }

    public void hide() {
        post(() -> setVisibility(GONE));
    }
}
