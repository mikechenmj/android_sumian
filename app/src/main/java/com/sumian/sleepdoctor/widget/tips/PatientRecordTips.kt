package com.sumian.sleepdoctor.widget.tips

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.sumian.sleepdoctor.R
import kotlinx.android.synthetic.main.lay_tips_my_record_view.view.*

/**
 * Created by sm
 *
 * on 2018/8/13
 *
 * desc:  我的档案模块,我的界面 tips 功能
 *
 */
class PatientRecordTips : LinearLayout, View.OnClickListener {

    private var mOnRecordTipsCallback: OnRecordTipsCallback? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context)
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.lay_tips_my_record_view, this)
        tv_sleep_record.setOnClickListener(this)
        tv_evaluation.setOnClickListener(this)
        tv_online_report.setOnClickListener(this)
    }

    fun setOnRecordTipsCallback(onRecordTipsCallback: OnRecordTipsCallback) {
        this.mOnRecordTipsCallback = onRecordTipsCallback
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_sleep_record -> {
                mOnRecordTipsCallback?.showSleepRecord()
            }
            R.id.tv_evaluation -> {
                mOnRecordTipsCallback?.showEvaluation()
            }
            R.id.tv_online_report -> {
                mOnRecordTipsCallback?.showOnlineReport()
            }
        }
    }


    interface OnRecordTipsCallback {

        fun showSleepRecord()

        fun showEvaluation()

        fun showOnlineReport()
    }


}