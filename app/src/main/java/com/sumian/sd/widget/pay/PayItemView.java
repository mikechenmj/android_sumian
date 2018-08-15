package com.sumian.sd.widget.pay;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumian.sd.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sm
 * on 2018/1/22.
 * desc:
 */

public class PayItemView extends LinearLayout implements View.OnClickListener {

    @BindView(R.id.iv_pay_icon)
    ImageView mIvPayIcon;

    @BindView(R.id.tv_pay_desc)
    TextView mTvPayDesc;

    @BindView(R.id.iv_pay_select)
    ImageView mIvPay;

    private OnPayWayCallback mOnPayWayCallback;

    public PayItemView(Context context) {
        this(context, null);
    }

    public PayItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PayItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PayItemView, defStyleAttr, 0);

        Drawable iconDrawable = a.getDrawable(R.styleable.PayItemView_icon);
        this.mIvPayIcon.setImageDrawable(iconDrawable);

        String text = a.getString(R.styleable.PayItemView_desc);
        mTvPayDesc.setText(text);

        boolean isSelect = a.getBoolean(R.styleable.PayItemView_is_select, false);
        mIvPay.setTag(isSelect ? true : null);
        mIvPay.setImageResource(isSelect ? R.mipmap.ic_group_pay_selected : R.mipmap.ic_group_pay_unselected);

        a.recycle();

        setOnClickListener(this);
    }

    private void init(Context context) {
        ButterKnife.bind(inflate(context, R.layout.lay_pay_divider_item, this));
        int padding = (int) context.getResources().getDimension(R.dimen.space_20);
        setPadding(padding, padding, padding, padding);
        setBackgroundColor(getResources().getColor(R.color.b2_color));
        setGravity(Gravity.CENTER);
        setOrientation(HORIZONTAL);
    }

    public void setOnPayWayCallback(OnPayWayCallback onPayWayCallback) {
        mOnPayWayCallback = onPayWayCallback;
    }

    public void select() {
        mIvPay.setTag(true);
        mIvPay.setImageResource(R.mipmap.ic_group_pay_selected);
        if (mOnPayWayCallback != null) {
            mOnPayWayCallback.onSelectPayWay(this);
        }
    }

    public void unSelect() {
        mIvPay.setTag(null);
        mIvPay.setImageResource(R.mipmap.ic_group_pay_unselected);
    }

    // @OnClick({R.id.iv_pay_select})
    @Override
    public void onClick(View v) {
        // switch (v.getId()) {
        //    case R.id.iv_pay_select:
        if (mIvPay.getTag() == null) {
            select();
        }
        //       break;
        //  default:
        //    break;
        // }
    }


    public interface OnPayWayCallback {

        void onSelectPayWay(View v);

    }
}
