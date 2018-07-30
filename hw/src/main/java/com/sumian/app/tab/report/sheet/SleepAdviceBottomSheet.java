package com.sumian.app.tab.report.sheet;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sumian.app.R;
import com.sumian.app.network.response.SleepAdvice;
import com.sumian.app.widget.BottomSheetView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jzz
 * on 2017/11/2.
 * <p>
 * desc:睡眠数据不足,睡眠建议弹窗
 */

public class SleepAdviceBottomSheet extends BottomSheetView implements View.OnClickListener {

    private static final String ARGS_SLEEP_ADVICE = "args_sleep_advice";

    TextView mTvSleepAdviceElement;
    TextView mTvSleepDataLess;
    TextView mTvSleepAdvice;

    private SleepAdvice mSleepAdvice;

    public static SleepAdviceBottomSheet newInstance(SleepAdvice sleepAdvice) {

        SleepAdviceBottomSheet bottomSheet = new SleepAdviceBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable(ARGS_SLEEP_ADVICE, sleepAdvice);
        bottomSheet.setArguments(args);
        return bottomSheet;
    }

    @Override
    protected void initBundle(Bundle arguments) {
        super.initBundle(arguments);
        this.mSleepAdvice = (SleepAdvice) arguments.getSerializable(ARGS_SLEEP_ADVICE);

    }

    @Override
    protected int getLayout() {
        return R.layout.hw_lay_bottom_sheet_sleep_advice;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mTvSleepAdviceElement = rootView.findViewById(R.id.tv_sleep_advice_element);
        mTvSleepDataLess = rootView.findViewById(R.id.tv_sleep_data_less);
        mTvSleepAdvice = rootView.findViewById(R.id.tv_sleep_advice);
        rootView.findViewById(R.id.bt_get).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mTvSleepAdviceElement.setText(mSleepAdvice.getFactor_detail());
        mTvSleepDataLess.setText(mSleepAdvice.getExplanation());
        mTvSleepAdvice.setText(mSleepAdvice.getAdvice());
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

}
