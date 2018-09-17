package com.sumian.hw.report.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qmuiteam.qmui.span.QMUIAlignMiddleImageSpan;
import com.qmuiteam.qmui.span.QMUIMarginImageSpan;
import com.sumian.sd.R;

/**
 * Created by sm
 * on 2018/5/17 14:51
 * desc:
 **/
public class SyncingReportView extends LinearLayout {

    ProgressBar mProgress;
    TextView mTvSyncingReport;

    private Runnable mDismissErrorRunnable;

    public SyncingReportView(Context context) {
        this(context, null);
    }

    public SyncingReportView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SyncingReportView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        setGravity(Gravity.CENTER);
        setOrientation(HORIZONTAL);
        //setMinimumHeight(getResources().getDimensionPixelSize(R.dimen.space_42));
        setVisibility(GONE);
    }

    private void initView(Context context) {
        View inflate = inflate(context, R.layout.hw_lay_sync_report_status_view, this);
        mProgress = inflate.findViewById(R.id.progress);
        mTvSyncingReport = inflate.findViewById(R.id.tv_syncing_report);
    }

    public void showSyncing() {
        runUiThread(() -> {
            mProgress.setVisibility(VISIBLE);
            setBackgroundColor(getResources().getColor(R.color.bt_hole_color));
            mTvSyncingReport.setTextColor(getResources().getColor(R.color.white));
            mTvSyncingReport.setText(R.string.syncing_report);
            show();
        });
    }

    public void showSyncError() {
        runUiThread(() -> {
            mProgress.setVisibility(GONE);

            Drawable drawable = getResources().getDrawable(R.mipmap.report_icon_explanation);
            drawable.setBounds(0, 0, getResources().getDimensionPixelSize(R.dimen.space_16), getResources().getDimensionPixelSize(R.dimen.space_16));

            String errorText = getResources().getString(R.string.syncing_error_result);

            QMUIMarginImageSpan imgSpan = new QMUIMarginImageSpan(drawable, QMUIAlignMiddleImageSpan.ALIGN_MIDDLE, 0, 0);
            SpannableString spannableString = new SpannableString("[icon]  " + errorText);
            spannableString.setSpan(imgSpan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            mTvSyncingReport.setTextColor(getResources().getColor(R.color.warn_color));
            mTvSyncingReport.setText(spannableString);
            setBackgroundColor(getResources().getColor(R.color.warn_bg_color));
            show();
            postDelayed(mDismissErrorRunnable = this::hide, 3000L);
        });
    }

    public void show() {
        runUiThread(() -> {
            if (mDismissErrorRunnable != null) {
                removeCallbacks(mDismissErrorRunnable);
            }
            setVisibility(VISIBLE);
        });
    }

    public void hide() {
        runUiThread(() -> {
            if (mDismissErrorRunnable != null) {
                removeCallbacks(mDismissErrorRunnable);
            }
            setVisibility(GONE);
        });
    }

    private void runUiThread(Runnable run) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            run.run();
        } else {
            post(run);
        }
    }
}
