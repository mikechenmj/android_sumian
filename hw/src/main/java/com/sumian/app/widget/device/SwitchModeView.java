package com.sumian.app.widget.device;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
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

public class SwitchModeView extends LinearLayout implements View.OnClickListener {

    @BindView(R.id.lay_monitor_warn)
    LinearLayout mLayMonitorWarn;

    @BindView(R.id.bt_switch_pa)
    Button mBtSwitchPa;

    private int mPaModeState;// 0x00 关闭  0x01开启

    private OnSwitchPaModeListener mOnSwitchPaModeListener;

    public SwitchModeView(Context context) {
        this(context, null);
    }

    public SwitchModeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchModeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        ButterKnife.bind(inflate(context, R.layout.hw_lay_sleepy_pa_mode, this));
    }

    public void setOnSwitchPaModeListener(OnSwitchPaModeListener onSwitchPaModeListener) {
        mOnSwitchPaModeListener = onSwitchPaModeListener;
    }

    @OnClick(R.id.bt_switch_pa)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_switch_pa:
                mOnSwitchPaModeListener.switchPaMode((this.mPaModeState == 0x00) ? 0x01 : 0x00);
                break;
            default:
                break;
        }
    }

    public void rollbackView() {
        updateForSnoopingMode(0x00, 0x00, 0x00);
        setVisibility(GONE);
    }

    public void updateForSnoopingMode(int snoopingModeState, int paModeState, int sleepyConnectedState) {
        this.mPaModeState = paModeState;
        mLayMonitorWarn.setVisibility(snoopingModeState == 0x01 ? VISIBLE : GONE);
        mBtSwitchPa.setText(R.string.turn_on_sleep_pa_mode);
        mBtSwitchPa.setVisibility(snoopingModeState == 0x00 && sleepyConnectedState == 0x01 ? VISIBLE : GONE);
        setVisibility(paModeState <= 0x00 ? VISIBLE : GONE);
    }

    public interface OnSwitchPaModeListener {

        void switchPaMode(int turnOn);
    }
}
