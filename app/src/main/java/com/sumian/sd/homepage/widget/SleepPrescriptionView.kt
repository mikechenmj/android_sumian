package com.sumian.sd.homepage.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.sumian.sd.R
import com.sumian.sd.homepage.bean.SleepPrescription
import kotlinx.android.synthetic.main.view_sleep_prescription.view.*

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/7/17 15:37
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class SleepPrescriptionView(context: Context, attributeSet: AttributeSet) : FrameLayout(context, attributeSet) {
    init {
        LayoutInflater.from(context).inflate(R.layout.view_sleep_prescription, this, true)
    }

    fun setPrescriptionData(data: SleepPrescription?) {
        val prescriptionIsNull = data?.getUpAt == null
        ll_no_sleep_prescription_hint.visibility = if (prescriptionIsNull) View.VISIBLE else View.GONE
        ll_sleep_prescription.visibility = if (!prescriptionIsNull) View.VISIBLE else View.GONE
        if (data != null) {
            tv_sleep_time.text = data.sleepAt
            tv_get_up_time.text = data.getUpAt
        }
    }

    fun setHasNewPrescription(hasNewPrescription: Boolean) {
        tv_new_prescription.visibility = if (hasNewPrescription) View.VISIBLE else View.GONE
    }
}