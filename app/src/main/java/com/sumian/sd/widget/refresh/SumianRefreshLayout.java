package com.sumian.sd.widget.refresh;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

import com.sumian.sd.R;

/**
 * Created by jzz
 * on 2018/2/1.
 * desc:
 */

public class SumianRefreshLayout extends SwipeRefreshLayout {

    private static final long DELAY_MILLS = 6 * 1000L;
    private Runnable mDismissRunnable = this::hideRefreshAnim;

    public SumianRefreshLayout(@NonNull Context context) {
        this(context, null);
    }

    public SumianRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        this.setColorSchemeResources(R.color.b3_color, R.color.b7_color, R.color.b8_color);
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
