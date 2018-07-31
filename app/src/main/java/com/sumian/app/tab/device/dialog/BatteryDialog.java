package com.sumian.app.tab.device.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;
import com.sumian.app.widget.BaseDialogFragment;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jzz
 * on 2017/11/3.
 * <p>
 * desc:
 */

public class BatteryDialog extends BaseDialogFragment implements View.OnClickListener {

    public static final String TAG = "BatteryDialog";

    private static final String ARGS_BATTERY_TYPE = "batteryType";
    private static final String ARGS_BATTERY = "battery";

    TextView mTvFirmwareTitle;

    private String mBatteryType;
    private int mBattery;

    public static BatteryDialog newInstance(String powerType, int power) {
        BatteryDialog batteryDialog = new BatteryDialog();
        Bundle args = new Bundle();
        args.putString(ARGS_BATTERY_TYPE, powerType);
        args.putInt(ARGS_BATTERY, power);
        batteryDialog.setArguments(args);
        return batteryDialog;
    }

    @Override
    protected void initBundle(Bundle arguments) {
        super.initBundle(arguments);
        this.mBatteryType = arguments.getString(ARGS_BATTERY_TYPE, "");
        this.mBattery = arguments.getInt(ARGS_BATTERY, 0);
    }

    @Override
    protected int getLayout() {
        return R.layout.hw_lay_dialog_power;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mTvFirmwareTitle = rootView.findViewById(R.id.tv_version_title);
        rootView.findViewById(R.id.tv_submit).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mTvFirmwareTitle.setText(String.format(Locale.getDefault(), getString(R.string.ring_power_less_hint), mBatteryType, mBattery, "%"));
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
