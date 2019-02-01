package com.sumian.sddoctor.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.sumian.sddoctor.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Created by jzz
 * on 2018/2/1.
 * desc:
 */

public class SumianSwipeRefreshLayout extends SwipeRefreshLayout {

    private static final long DELAY_MILLS = 6 * 1000L;
    private Runnable mDismissRunnable = this::hideRefreshAnim;

    public SumianSwipeRefreshLayout(@NonNull Context context) {
        this(context, null);
    }

    public SumianSwipeRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        this.setColorSchemeResources(R.color.b3_color, R.color.b7_color);
    }

    public void showRefreshAnim() {
        setRefreshing(true);
        removeCallbacks(mDismissRunnable);
        postDelayed(mDismissRunnable, DELAY_MILLS);
    }

    public void hideRefreshAnim() {
        removeCallbacks(mDismissRunnable);
        setRefreshing(false);
    }
}
