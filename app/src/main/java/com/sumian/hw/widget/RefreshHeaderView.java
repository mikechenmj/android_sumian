package com.sumian.hw.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;

/**
 * Created by jzz
 * on 2018/1/8.
 * desc:
 */

public class RefreshHeaderView extends LinearLayout {

    ImageView mIvLoadingIndicator;
    ProgressBar mPgLoading;
    TextView mTvMsg;

    public RefreshHeaderView(Context context) {
        this(context, null);
    }

    public RefreshHeaderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshHeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.hw_lay_refresh_header_view, this, true);
        mIvLoadingIndicator = inflate.findViewById(R.id.iv_loading_indicator);
        mPgLoading = inflate.findViewById(R.id.pg_loading);
        mTvMsg = inflate.findViewById(R.id.tv_msg);
    }

    public void updatePullStatus(int distance) {
        if (distance > 150 && getTag() != null) {
            setTag(true);
            mIvLoadingIndicator.setImageResource(R.mipmap.ic_report_release);
            mIvLoadingIndicator.setVisibility(VISIBLE);
            mPgLoading.setVisibility(INVISIBLE);
            mTvMsg.setText(R.string.release_pull_to_load_data);
        } else if (distance <= 0 && getTag() == null) {
            mIvLoadingIndicator.setImageResource(R.mipmap.ic_report_dropdown);
            mIvLoadingIndicator.setVisibility(VISIBLE);
            mPgLoading.setVisibility(INVISIBLE);
            mTvMsg.setText(R.string.pull_down_refresh_data);
            setTag(null);
        }
    }

    public void enableToRefresh() {
        mIvLoadingIndicator.setImageResource(R.mipmap.ic_report_release);
        mIvLoadingIndicator.setVisibility(VISIBLE);
        mPgLoading.setVisibility(INVISIBLE);
        mTvMsg.setText(R.string.release_pull_to_load_data);
    }

    public void refreshing() {
        mIvLoadingIndicator.setImageResource(R.mipmap.ic_report_dropdown);
        mIvLoadingIndicator.setVisibility(INVISIBLE);
        mPgLoading.setVisibility(VISIBLE);
        mTvMsg.setText(R.string.load_data);
        setTag(null);
    }
}
