package com.sumian.sd.widget.tips

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.sumian.sd.R
import kotlinx.android.synthetic.main.lay_tips_my_service_view.view.*

/**
 * Created by sm
 *
 * on 2018/8/13
 *
 * desc:  我的服务模块,我的界面 tips 功能
 *
 */
class PatientServiceTips : LinearLayout, View.OnClickListener {

    private var mOnServiceTipsCallback: OnServiceTipsCallback? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context)
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.lay_tips_my_service_view, this)
        tv_graphic.setOnClickListener(this)
        tv_tel.setOnClickListener(this)
        tv_diary_evaluation.setOnClickListener { mOnServiceTipsCallback?.onDiaryEvaluationClick() }
    }


    fun setOnServiceTipsCallback(onServiceTipsCallback: OnServiceTipsCallback) {
        this.mOnServiceTipsCallback = onServiceTipsCallback
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_graphic -> {
                mOnServiceTipsCallback?.showGraphicService()
            }
            R.id.tv_tel -> {
                mOnServiceTipsCallback?.showTelService()
            }
        }
    }


    interface OnServiceTipsCallback {

        fun showGraphicService()
        fun showTelService()
        fun onDiaryEvaluationClick()

    }


}