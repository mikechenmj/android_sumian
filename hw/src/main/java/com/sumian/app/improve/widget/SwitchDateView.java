package com.sumian.app.improve.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumian.app.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sm
 * on 2018/3/6.
 * desc:
 */

public class SwitchDateView extends FrameLayout implements View.OnClickListener {

    @BindView(R.id.iv_pre)
    ImageView mIvPre;
    @BindView(R.id.tv_date)
    TextView mTvDate;
    @BindView(R.id.iv_next)
    ImageView mIvNext;

    private long mUnixTime;

    private OnSwitchDateListener mOnSwitchDateListener;

    public SwitchDateView(Context context) {
        this(context, null);
    }

    public SwitchDateView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchDateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundColor(getResources().getColor(R.color.pick_bg_color));
        initView(context);
        this.mTvDate.setText(new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date()));
    }

    private void initView(Context context) {
        ButterKnife.bind(inflate(context, R.layout.hw_lay_switch_date_view, this));
    }

    public void setOnSwitchDateListener(OnSwitchDateListener onSwitchDateListener) {
        mOnSwitchDateListener = onSwitchDateListener;
    }

    @OnClick({R.id.iv_pre, R.id.iv_next})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_pre:
                if (mOnSwitchDateListener != null) {
                    mOnSwitchDateListener.scrollToTime(mUnixTime - 60 * 60 * 24);
                }
                break;
            case R.id.iv_next:
                if (mOnSwitchDateListener != null) {
                    mOnSwitchDateListener.scrollToTime(mUnixTime + 60 * 60 * 24);
                }
                break;
            default:
                break;
        }
    }

    public long getTodayUnixTime() {
        Calendar instance = Calendar.getInstance();
        int year = instance.get(Calendar.YEAR);
        int month = instance.get(Calendar.MONTH);
        int date = instance.get(Calendar.DATE);
        instance.set(year, month, date, 0, 0, 0);
        return instance.getTimeInMillis() / 1000L;
    }

    public long getUnixTime() {
        return mUnixTime;
    }

    public void setUnixTime(long unixTime) {
        this.mUnixTime = unixTime;
        if (getTodayUnixTime() == unixTime) {
            mIvNext.setVisibility(INVISIBLE);
        } else {
            mIvNext.setVisibility(VISIBLE);
        }
        this.mTvDate.setText(new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date(unixTime * 1000L)));
    }

    public interface OnSwitchDateListener {

        void scrollToTime(long unixTime);

    }
}
