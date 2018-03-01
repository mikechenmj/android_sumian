package com.sumian.sleepdoctor.widget.pay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.chat.widget.CustomPopWindow;

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


    @BindView(R.id.tv_label)
    TextView mTvLabel;

    @BindView(R.id.iv_faq)
    ImageView mIvPayFaq;

    @BindView(R.id.iv_reduce_duration)
    ImageView mIvReduceDuration;

    @BindView(R.id.tv_duration)
    TextView mTvDuration;

    @BindView(R.id.iv_add_duration)
    ImageView mIvAddDuration;

    @BindView(R.id.tv_money)
    TextView mTvMoney;

    private int mCurrentDuration = 1;

    private double mDefaultMoney = 0.00f;
    private double mCurrentMoney = 0.00f;

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

    public void setOnMoneyChangeCallback(OnMoneyChangeCallback onMoneyChangeCallback) {
        mOnMoneyChangeCallback = onMoneyChangeCallback;
    }

    @OnClick({R.id.iv_faq, R.id.iv_reduce_duration, R.id.iv_add_duration})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_faq:

                @SuppressLint("InflateParams") View rootView = LayoutInflater.from(v.getContext()).inflate(R.layout.lay_pop_pay_faq, null, false);

                CustomPopWindow popWindow = new CustomPopWindow.PopupWindowBuilder(v.getContext())
                        .setView(rootView)//显示的布局，还可以通过设置一个View
                        //     .size(600,400) //设置显示的大小，不设置就默认包裹内容
                        .setFocusable(true)//是否获取焦点，默认为ture
                        .setOutsideTouchable(true)//是否PopupWindow 以外触摸dissmiss
                        .create()//创建PopupWindow
                        .showAsDropDown(mIvPayFaq, -3 * (mIvPayFaq.getWidth()), (int) (-4.4 * mIvPayFaq.getHeight()), Gravity.TOP | Gravity.CENTER);//显示PopupWindow

                v.postDelayed(popWindow::dismiss, 3000);
                rootView.setOnClickListener(v1 -> popWindow.dismiss());

                break;
            case R.id.iv_reduce_duration:
                if (mCurrentDuration == 1) {
                    mCurrentDuration = 1;
                } else {
                    mCurrentDuration--;
                }
                break;
            case R.id.iv_add_duration:
                if (mCurrentDuration < 6) {
                    mCurrentDuration++;
                }
                break;
            default:
                break;
        }

        if (mCurrentDuration > 1) {
            mIvReduceDuration.setEnabled(true);
            mIvReduceDuration.setImageResource(R.mipmap.group_pay_btn_plus);
        } else {
            mIvReduceDuration.setEnabled(false);
            mIvReduceDuration.setImageResource(R.mipmap.group_pay_btn_plus_disabled);
        }

        if (mCurrentDuration < 6) {
            mIvAddDuration.setEnabled(true);
            mIvAddDuration.setImageResource(R.mipmap.group_pay_btn_minus);
        } else {
            mIvAddDuration.setEnabled(false);
            mIvAddDuration.setImageResource(R.mipmap.group_pay_btn_minus_disabled);
        }

        mTvDuration.setText(String.valueOf(mCurrentDuration));
        mCurrentMoney = mDefaultMoney * mCurrentDuration;

        if (mOnMoneyChangeCallback != null) {
            mOnMoneyChangeCallback.onMoneyChange(mCurrentMoney);
        }
        updateMoney(mCurrentMoney);
    }

    private void updateMoney(double currentMoney) {
        this.mCurrentMoney = currentMoney;
        formatMoney(mTvMoney, mCurrentMoney);
    }

    public void setDefaultMoney(double defaultMoney) {
        this.mDefaultMoney = defaultMoney;
        this.mCurrentMoney = mDefaultMoney * mCurrentDuration;
        formatMoney(mTvMoney, mDefaultMoney);
    }

    private void formatMoney(TextView tv, double money) {
        tv.setText(String.format(Locale.getDefault(), "%.2f", money / 100.00f));
    }

    public int getCurrentDuration() {
        return mCurrentDuration;
    }

    public double getCurrentMoney() {
        return mCurrentMoney;
    }

    public double getDefaultMoney() {
        return mDefaultMoney;
    }

    public interface OnMoneyChangeCallback {

        void onMoneyChange(double money);
    }
}
