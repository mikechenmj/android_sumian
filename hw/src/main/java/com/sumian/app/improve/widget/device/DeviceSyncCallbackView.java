package com.sumian.app.improve.widget.device;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.qmuiteam.qmui.util.QMUISpanHelper;
import com.sumian.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jzz
 * on 2017/12/13.
 * desc:设备睡眠特征同步结果 view
 */

public class DeviceSyncCallbackView extends FrameLayout implements Runnable {

    public static final int SHOW_SYNC_RESULT_DURATION = 3000;
    @BindView(R.id.tv_loading)
    TextView mTvLoading;

    public DeviceSyncCallbackView(Context context) {
        this(context, null);
    }

    public DeviceSyncCallbackView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeviceSyncCallbackView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setVisibility(GONE);
        init(context);
    }

    private void init(Context context) {
        ButterKnife.bind(inflate(context, R.layout.hw_lay_syncing_sleep_cha_view, this));
    }

    public void showSyncSuccess() {
        removeCallbacks(this);
        String content = getResources().getString(R.string.loading_success);
        mTvLoading.setTextColor(getResources().getColor(R.color.refresh_loading_success_text_color));
        CharSequence charSequence = QMUISpanHelper.generateSideIconText(true, getResources().getDimensionPixelSize(R.dimen.space_6), content, getResources().getDrawable(R.mipmap.ic_report_success));
        mTvLoading.setText(charSequence);
        setBackgroundColor(getResources().getColor(R.color.refresh_loading_success_bg_color));
        showSyncResultForAWhile();
    }

    public void showSyncFailed(String msg) {
        removeCallbacks(this);
        mTvLoading.setTextColor(getResources().getColor(R.color.battery_top_less_color));
        mTvLoading.setText(msg);
        setBackgroundColor(getResources().getColor(R.color.warn_bg_color));
        showSyncResultForAWhile();
    }

    private void showSyncResultForAWhile() {
        setVisibility(VISIBLE);
        postDelayed(this, SHOW_SYNC_RESULT_DURATION);
    }

    @Override
    public void run() {
        setVisibility(GONE);
    }
}
