package com.sumian.hw.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.sumian.sleepdoctor.R;
import com.sumian.hw.setting.activity.ConfigActivity;

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
        View inflate = LayoutInflater.from(context).inflate(R.layout.hw_lay_register_rule, this, true);
        inflate.findViewById(R.id.tv_rule_user_agreement).setOnClickListener(this);
        inflate.findViewById(R.id.tv_privacy_policy).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_rule_user_agreement) {
            ConfigActivity.show(v.getContext(), ConfigActivity.REGISTER_USER_AGREEMENT_TYPE);
        } else if (i == R.id.tv_privacy_policy) {
            ConfigActivity.show(v.getContext(), ConfigActivity.REGISTER_PRIVACY_POLICY_TYPE);
        }
    }
}
