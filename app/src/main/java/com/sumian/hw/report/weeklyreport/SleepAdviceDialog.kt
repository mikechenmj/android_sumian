package com.sumian.hw.report.weeklyreport

import android.content.Context
import android.support.v7.app.AppCompatDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.sumian.sd.R
import com.sumian.hw.report.widget.bean.SleepAdvice

/**
 * Created by jzz
 * on 2017/11/2.
 *
 *
 * desc:睡眠数据不足,睡眠建议弹窗
 */

class SleepAdviceDialog(context: Context) : AppCompatDialog(context, R.style.SumianDialog), View.OnClickListener {

    private var mTvSleepAdviceElement: TextView? = null
    private var mTvSleepDataLess: TextView? = null
    private var mTvSleepAdvice: TextView? = null

    private var mSleepAdvice: SleepAdvice? = null

    init {
        val layoutId = R.layout.lay_bottom_sheet_sleep_advice_day
        val rootView = LayoutInflater.from(context).inflate(layoutId, null, false)
        mTvSleepAdviceElement = rootView.findViewById(R.id.tv_sleep_advice_element)
        mTvSleepDataLess = rootView.findViewById(R.id.tv_sleep_data_less)
        mTvSleepAdvice = rootView.findViewById(R.id.tv_sleep_advice)
        rootView.findViewById<View>(R.id.iv_close).setOnClickListener(this)
        setContentView(rootView)
    }

    fun setAdvice(advice: SleepAdvice): SleepAdviceDialog {
        this.mSleepAdvice = advice
        mTvSleepAdviceElement?.text = mSleepAdvice?.factor_detail
        mTvSleepDataLess?.text = mSleepAdvice?.explanation
        mTvSleepAdvice?.text = mSleepAdvice?.advice
        return this
    }

    override fun onClick(v: View) {
        cancel()
    }

}
