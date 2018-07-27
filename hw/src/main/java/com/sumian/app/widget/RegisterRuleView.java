package com.sumian.app.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.sumian.app.R;
import com.sumian.app.setting.activity.ConfigActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jzz
 * on 2017/12/18.
 * desc:
 */

public class RegisterRuleView extends LinearLayout implements View.OnClickListener {

    public RegisterRuleView(Context context) {
        this(context, null);
    }

    public RegisterRuleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RegisterRuleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        ButterKnife.bind(LayoutInflater.from(context).inflate(R.layout.hw_lay_register_rule, this, true));
    }

    @OnClick({R.id.tv_rule_user_agreement, R.id.tv_privacy_policy})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_rule_user_agreement:
                ConfigActivity.show(v.getContext(), ConfigActivity.REGISTER_USER_AGREEMENT_TYPE);
                break;
            case R.id.tv_privacy_policy:
                ConfigActivity.show(v.getContext(), ConfigActivity.REGISTER_PRIVACY_POLICY_TYPE);
                break;
        }
    }
}
