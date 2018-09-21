package com.sumian.hw.report.sheet

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.sumian.hw.network.response.SleepAdvice
import com.sumian.hw.widget.BottomSheetView
import com.sumian.sd.R
import com.sumian.sd.theme.three.SkinConfig

/**
 * Created by jzz
 * on 2017/11/2.
 *
 *
 * desc:睡眠数据不足,睡眠建议弹窗
 */

class SleepAdviceBottomSheet : BottomSheetView(), View.OnClickListener {

    private var mTvSleepAdviceElement: TextView? = null
    private var mTvSleepDataLess: TextView? = null
    private var mTvSleepAdvice: TextView? = null

    private var mSleepAdvice: SleepAdvice? = null

    override fun initBundle(arguments: Bundle) {
        super.initBundle(arguments)
        this.mSleepAdvice = arguments.getSerializable(ARGS_SLEEP_ADVICE) as SleepAdvice
    }

    override fun getLayout(): Int {
        return if (SkinConfig.isInNightMode(context)) R.layout.lay_bottom_sheet_sleep_advice_night else R.layout.lay_bottom_sheet_sleep_advice_day
    }

    override fun initView(rootView: View) {
        super.initView(rootView)
        mTvSleepAdviceElement = rootView.findViewById(R.id.tv_sleep_advice_element)
        mTvSleepDataLess = rootView.findViewById(R.id.tv_sleep_data_less)
        mTvSleepAdvice = rootView.findViewById(R.id.tv_sleep_advice)
        rootView.findViewById<View>(R.id.iv_close).setOnClickListener(this)
    }

    override fun initData() {
        super.initData()
        mTvSleepAdviceElement?.text = mSleepAdvice?.factor_detail
        mTvSleepDataLess?.text = mSleepAdvice?.explanation
        mTvSleepAdvice?.text = mSleepAdvice?.advice
    }

    override fun onClick(v: View) {
        dismissAllowingStateLoss()
    }

    companion object {

        private const val ARGS_SLEEP_ADVICE = "args_sleep_advice"

        @JvmStatic
        fun newInstance(sleepAdvice: SleepAdvice): SleepAdviceBottomSheet {

            val bottomSheet = SleepAdviceBottomSheet()
            val args = Bundle()
            args.putSerializable(ARGS_SLEEP_ADVICE, sleepAdvice)
            bottomSheet.arguments = args
            return bottomSheet
        }
    }

}
