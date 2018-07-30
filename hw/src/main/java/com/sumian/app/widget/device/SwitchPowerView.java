package com.sumian.app.widget.device;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sumian.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jzz
 * on 2017/11/29.
 * <p>
 * desc:
 */

public class SwitchPowerView extends LinearLayout implements View.OnClickListener {

    ImageView mIvPowerIndicator;
    ImageView mIvSwitchSleepyPower;

    private int mPower = 0x00;//0x00 off 0x01 weak  0x02 strong

    private OnSwitchPowerListener mOnSwitchPowerListener;

    public SwitchPowerView(Context context) {
        this(context, null);
    }

    public SwitchPowerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchPowerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setVisibility(GONE);
        setOrientation(VERTICAL);
        View inflate = inflate(context, R.layout.hw_lay_switch_power, this);
        ButterKnife.bind(inflate);
        mIvPowerIndicator = inflate.findViewById(R.id.iv_power_indicator);
        mIvSwitchSleepyPower = inflate.findViewById(R.id.iv_switch_sleepy_power);
        inflate.findViewById(R.id.iv_switch_sleepy_power).setOnClickListener(this);
    }

    public void setOnSwitchPowerListener(OnSwitchPowerListener onSwitchPowerListener) {
        mOnSwitchPowerListener = onSwitchPowerListener;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        int power = this.mPower;
        if (power == 0x01) {
            mOnSwitchPowerListener.switchPower(power = 0x02);
        } else {
            mOnSwitchPowerListener.switchPower(power = 0x01);
        }
        updatePower(power);
    }

    public void rollbackPower() {
        updatePower(this.mPower = 0x00);
        setVisibility(GONE);
    }

    public void updatePower(int power) {
        if (power > 0x00) {
            mIvPowerIndicator.setImageResource((this.mPower = power) == 0x01 ? R.mipmap.ic_equip_switch_weak : R.mipmap.ic_equip_switch_strong);
            mIvSwitchSleepyPower.setRotation(power == 0x01 ? 180 : 0);
        }
        setVisibility(power > 0x00 ? VISIBLE : GONE);
    }

    public interface OnSwitchPowerListener {

        void switchPower(int power);
    }


}
