package com.sumian.sddoctor.patient.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.sumian.sddoctor.R
import com.sumian.sddoctor.util.ResUtils
import kotlinx.android.synthetic.main.lay_patient_sort_view.view.*

/**
 * Created by sm
 *
 * on 2018/8/29
 *
 * desc: 患者排序 view   按患者分级/面诊患者  进行排序
 *
 */
class PatientSortView : LinearLayout, View.OnClickListener {

    private var onPatientSortViewCallback: OnPatientSortViewCallback? = null

    private var isSortByLevel = true  //true  患者分级  false 面诊患者

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        orientation = LinearLayout.VERTICAL
        setBackgroundColor(ResUtils.getColor(R.color.alpha5_color))
        dismiss()
        inflateLayout(context)
    }

    private fun inflateLayout(context: Context) {
        View.inflate(context, R.layout.lay_patient_sort_view, this)
        tv_patient_level.setOnClickListener(this)
        tv_face_patient.setOnClickListener(this)
        setOnClickListener(this)
    }

    fun setOnPatientSortViewCallback(onPatientSortViewCallback: OnPatientSortViewCallback) {
        this.onPatientSortViewCallback = onPatientSortViewCallback
    }

    fun show() {
        visibility = View.VISIBLE
    }

    fun dismiss() {
        visibility = View.GONE
        onPatientSortViewCallback?.dismissCallback()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_patient_level -> {
                isSortByLevel = true
                isSelect(tv_patient_level, tv_face_patient)
                onPatientSortViewCallback?.sortByLevelCallback()
            }
            R.id.tv_face_patient -> {
                isSortByLevel = false
                isSelect(tv_face_patient, tv_patient_level)
                onPatientSortViewCallback?.sortByFaceCallback()
            }
        }
        dismiss()
    }

    private fun isSelect(selectView: TextView, unSelectView: TextView) {
        selectView.setTextColor(ResUtils.getColor(R.color.b3_color))
        unSelectView.setTextColor(ResUtils.getColor(R.color.t1_color))
    }

    fun isSortByLevel(): Boolean {
        return isSortByLevel
    }

    interface OnPatientSortViewCallback {

        fun sortByLevelCallback()

        fun sortByFaceCallback()

        fun dismissCallback()

    }
}