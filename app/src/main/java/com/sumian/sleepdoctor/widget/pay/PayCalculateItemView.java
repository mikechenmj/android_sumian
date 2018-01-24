package com.sumian.sleepdoctor.widget.pay;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sm
 * on 2018/1/22.
 * desc:
 */

public class PayCalculateItemView extends LinearLayout implements View.OnClickListener {

    private static final String TAG = PayCalculateItemView.class.getSimpleName();

    @BindView(R.id.iv_reduce_duration)
    ImageView mIvReduceDuration;

    @BindView(R.id.tv_duration)
    TextView mTvDuration;

    @BindView(R.id.iv_add_duration)
    ImageView mIvAddDuration;

    @BindView(R.id.tv_money)
    TextView mTvMoney;

    private int mCurrentDuration = 1;
    private float mCurrentMoney = 0.00f;
    private float mDefaultMoney = 0.00f;

    private OnMoneyChangeCallback mOnMoneyChangeCallback;

    public PayCalculateItemView(Context context) {
        this(context, null);
    }

    public PayCalculateItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PayCalculateItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        ButterKnife.bind(inflate(context, R.layout.lay_pay_calculate_item_view, this));
        setGravity(Gravity.CENTER);
        setOrientation(VERTICAL);
    }

    public PayCalculateItemView setOnMoneyChangeCallback(OnMoneyChangeCallback onMoneyChangeCallback) {
        mOnMoneyChangeCallback = onMoneyChangeCallback;
        return this;
    }

    @OnClick({R.id.iv_reduce_duration, R.id.iv_add_duration})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_reduce_duration:
                if (mCurrentDuration == 0) {
                    mCurrentDuration = 0;
                } else {
                    mCurrentDuration--;
                }
                break;
            case R.id.iv_add_duration:
                mCurrentDuration++;
                break;
            default:
                break;
        }

        if (mCurrentDuration > 0) {
            mIvReduceDuration.setEnabled(true);
            mIvReduceDuration.setImageResource(R.mipmap.group_pay_btn_plus);
        } else {
            mIvReduceDuration.setEnabled(false);
            mIvReduceDuration.setImageResource(R.mipmap.group_pay_btn_plus_disabled);
        }

        mTvDuration.setText(String.valueOf(mCurrentDuration));
        mCurrentMoney = mDefaultMoney * mCurrentDuration;

        if (mOnMoneyChangeCallback != null) {
            mOnMoneyChangeCallback.onMoneyChange(mCurrentMoney);
        }
        updateMoney(mCurrentMoney);
    }

    private void updateMoney(float currentMoney) {
        this.mCurrentMoney = currentMoney;
        formatMoney(mTvMoney, mCurrentMoney);
    }

    public void setDefaultMoney(float defaultMoney) {
        this.mDefaultMoney = defaultMoney;
        this.mCurrentMoney = mDefaultMoney * mCurrentDuration;
        formatMoney(mTvMoney, mDefaultMoney);
    }

    private void formatMoney(TextView tv, float money) {
        tv.setText(String.format(Locale.getDefault(), "%.2f", money));
    }

    public int getCurrentDuration() {
        return mCurrentDuration;
    }

    public float getCurrentMoney() {
        return mCurrentMoney;
    }

    public float getDefaultMoney() {
        return mDefaultMoney;
    }

    public interface OnMoneyChangeCallback {

        void onMoneyChange(float money);
    }
}
