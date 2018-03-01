package com.sumian.sleepdoctor.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.sumian.sleepdoctor.R;

import net.qiujuer.genius.ui.widget.Button;
import net.qiujuer.genius.ui.widget.ImageView;
import net.qiujuer.genius.ui.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jzz
 * on 2018/1/19.
 * desc:
 */

public class GroupErrorView extends LinearLayout implements View.OnClickListener {

    @BindView(R.id.iv_error)
    ImageView mIvError;
    @BindView(R.id.tv_error)
    TextView mTvError;

    @BindView(R.id.bt_refresh)
    Button mBtRefresh;

    private OnErrorRefreshListener mOnErrorRefreshListener;


    public GroupErrorView(Context context) {
        this(context, null);
    }

    public GroupErrorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GroupErrorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setGravity(Gravity.CENTER);
        setOrientation(VERTICAL);
        setBackgroundColor(getResources().getColor(R.color.b1_color));
        setVisibility(GONE);
        ButterKnife.bind(inflate(context, R.layout.lay_error_view, this));
    }

    public GroupErrorView setOnErrorRefreshListener(OnErrorRefreshListener onErrorRefreshListener) {
        mOnErrorRefreshListener = onErrorRefreshListener;
        return this;
    }

    public void showError() {
        mIvError.setImageResource(R.mipmap.group_synchronizationfailed);
        mTvError.setText(R.string.network_error);
        mBtRefresh.setVisibility(VISIBLE);
    }

    public void showRequest() {
        mIvError.setImageResource(R.mipmap.group_synchronizing);
        mTvError.setText(R.string.syncing);
        mBtRefresh.setVisibility(VISIBLE);
    }

    public void hideError() {
        mIvError.setImageResource(R.mipmap.group_synchronizing);
        mTvError.setText(R.string.syncing);
        mBtRefresh.setVisibility(INVISIBLE);
    }

    @OnClick(R.id.bt_refresh)
    @Override
    public void onClick(View v) {
        hideError();
        if (mOnErrorRefreshListener != null) {
            mOnErrorRefreshListener.onRefresh();
        }
    }


    interface OnErrorRefreshListener {

        void onRefresh();
    }
}
