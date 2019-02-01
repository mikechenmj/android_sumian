@file:Suppress("DEPRECATION")

package com.sumian.sddoctor.service.cbti.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.sumian.common.image.ImageLoader
import com.sumian.sddoctor.R
import com.sumian.sddoctor.patient.activity.PatientInfoActivity
import com.sumian.sddoctor.patient.bean.Patient
import com.sumian.sddoctor.util.TimeUtil
import kotlinx.android.synthetic.main.lay_item_patient_cbti_progress.view.*
import java.util.*

class CBTIProgressPatientView : LinearLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context)
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.lay_item_patient_cbti_progress, this)
    }

    fun invalidCBTIProgressPatient(cbtiProgressType: String, patient: Patient, isHideDivider: Boolean) {
        setOnClickListener {
            PatientInfoActivity.show(context, patient.id, patient.consulted)
        }

        load(patient.avatar)

        patient.invalidTagView(iv_patient_level)

        tv_name.text = patient.getNameOrNickname()

        tv_sex_and_age.text = patient.formatCBTIProgress(cbtiProgressType)
        tv_sex_and_age.visibility = View.VISIBLE
        View.VISIBLE


        tv_add_date.text = TimeUtil.formatLineToday(Date().apply {
            time = patient.start_at * 1000L
            tv_add_date.visibility = View.VISIBLE
        })

        v_divider.visibility = if (isHideDivider) View.INVISIBLE else View.VISIBLE
    }

    private fun load(url: String?) {
        ImageLoader.loadImage(url
                ?: "", iv_avatar, R.mipmap.ic_info_avatar_patient, R.mipmap.ic_info_avatar_patient)
    }
}
