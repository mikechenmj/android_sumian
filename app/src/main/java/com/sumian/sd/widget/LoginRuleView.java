package com.sumian.sd.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sumian.sd.R;
import com.sumian.sd.h5.H5Uri;
import com.sumian.sd.h5.SimpleWebActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jzz
 * on 2017/12/18.
 * desc:
 */

public class LoginRuleView extends LinearLayout implements View.OnClickListener {

    @BindView(R.id.iv)
    ImageView mIv;

    private OnCheckedListener mOnCheckedListener;

    public LoginRuleView(Context context) {
        this(context, null);
    }

    public LoginRuleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoginRuleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        setGravity(Gravity.CENTER);
        setOrientation(HORIZONTAL);
        mIv.setActivated(true);
    }

    private void init(Context context) {
        ButterKnife.bind(inflate(context, R.layout.lay_register_rule, this));
    }

    public void setOnCheckedListener(OnCheckedListener onCheckedListener) {
        this.mOnCheckedListener = onCheckedListener;
    }

    public boolean isChecked() {
        return mIv.isActivated();
    }

    @OnClick({R.id.iv, R.id.tv_rule_user_agreement, R.id.tv_privacy_policy})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv:
                mIv.setActivated(!mIv.isActivated());
                if (mOnCheckedListener != null) {
                    mOnCheckedListener.onChecked(mIv.isActivated());
                }
                break;
            case R.id.tv_rule_user_agreement:
                SimpleWebActivity.launch(getContext(), H5Uri.USER_AGREEMENT_URL);
                break;
            case R.id.tv_privacy_policy:
                SimpleWebActivity.launch(getContext(), H5Uri.USER_POLICY_URL);
                break;
            default:
                break;
        }
    }

    public interface OnCheckedListener {

        void onChecked(boolean isChecked);
    }
}
