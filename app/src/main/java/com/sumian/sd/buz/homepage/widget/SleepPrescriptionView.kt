package com.sumian.sd.buz.homepage.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.sumian.sd.R
import com.sumian.sd.buz.homepage.bean.SleepPrescription
import com.sumian.sd.buz.homepage.bean.SleepPrescriptionStatus
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

    private fun setPrescriptionData(data: SleepPrescription?) {
        val prescriptionIsNull = data?.getUpAt == null
        ll_no_sleep_prescription_hint.visibility = if (prescriptionIsNull) View.VISIBLE else View.GONE
        ll_sleep_prescription.visibility = if (!prescriptionIsNull) View.VISIBLE else View.GONE
        if (data != null) {
            tv_sleep_time.text = data.sleepAt
            tv_get_up_time.text = data.getUpAt
        }
    }

    private fun setHasNewPrescription(hasNewPrescription: Boolean) {
        tv_new_prescription.visibility = if (hasNewPrescription) View.VISIBLE else View.GONE
    }

    fun setPrescriptionData(data: SleepPrescriptionStatus) {
        val meta = data.meta
        val days_remain = meta.prescription.days_remain
        setHasNewPrescription(meta.update)
        setPrescriptionData(meta.prescription.data)
        tv_remain_days.text = context.resources.getString(R.string.n_days_later_update_prescription, days_remain)
        tv_remain_days.visibility = if (days_remain > 0) View.VISIBLE else View.GONE
    }
}