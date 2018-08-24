package com.sumian.hw.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumian.sd.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by sm
 * on 2018/3/6.
 * desc:
 */

public class SwitchDateView extends FrameLayout implements View.OnClickListener {

    ImageView mIvPre;
    TextView mTvDate;
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
        View inflate = inflate(context, R.layout.hw_lay_switch_date_view, this);
        mIvPre = inflate.findViewById(R.id.iv_pre);
        mTvDate = inflate.findViewById(R.id.tv_date);
        mIvNext = inflate.findViewById(R.id.iv_next);
        inflate.findViewById(R.id.iv_pre).setOnClickListener(this);
        inflate.findViewById(R.id.iv_next).setOnClickListener(this);
    }

    public void setOnSwitchDateListener(OnSwitchDateListener onSwitchDateListener) {
        mOnSwitchDateListener = onSwitchDateListener;
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.iv_pre) {
            if (mOnSwitchDateListener != null) {
                mOnSwitchDateListener.scrollToTime(mUnixTime - 60 * 60 * 24);
            }
        } else if (i == R.id.iv_next) {
            if (mOnSwitchDateListener != null) {
                mOnSwitchDateListener.scrollToTime(mUnixTime + 60 * 60 * 24);
            }
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
