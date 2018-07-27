package com.sumian.sleepdoctor.widget.pay;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.sumian.sleepdoctor.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sm
 * on 2018/1/22.
 * desc:
 */

public class PayItemGroupView extends LinearLayout implements PayItemView.OnPayWayCallback {

    @BindView(R.id.wechat_pay_item)
    PayItemView mWechatPayWay;

    @BindView(R.id.alipay_pay_item)
    PayItemView mAlipayWay;

    private OnSelectPayWayListener mOnSelectPayWayListener;

    public PayItemGroupView(Context context) {
        this(context, null);
    }

    public PayItemGroupView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PayItemGroupView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        ButterKnife.bind(inflate(context, R.layout.lay_pay_item_group, this));
        setGravity(Gravity.CENTER);
        setOrientation(VERTICAL);
        mWechatPayWay.setOnPayWayCallback(this);
        mAlipayWay.setOnPayWayCallback(this);

    }

    public void setOnSelectPayWayListener(OnSelectPayWayListener onSelectPayWayListener) {
        mOnSelectPayWayListener = onSelectPayWayListener;
    }

    @Override
    public void onSelectPayWay(View v) {
        switch (v.getId()) {
            case R.id.wechat_pay_item:
                mAlipayWay.unSelect();
                if (mOnSelectPayWayListener != null) {
                    mOnSelectPayWayListener.onSelectWechatPayWay();
                }
                break;
            case R.id.alipay_pay_item:
                mWechatPayWay.unSelect();
                if (mOnSelectPayWayListener != null) {
                    mOnSelectPayWayListener.onSelectAlipayWay();
                }
                break;
            default:
                break;
        }
    }

    public interface OnSelectPayWayListener {

        void onSelectWechatPayWay();

        void onSelectAlipayWay();
    }
}
