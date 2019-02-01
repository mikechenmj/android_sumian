package com.sumian.sddoctor.patient.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.sumian.sddoctor.R
import kotlinx.android.synthetic.main.lay_patient_empty_view.view.*

class PatientEmptyView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr), View.OnClickListener {

    private var mOnEmptyPatientCallback: OnEmptyPatientCallback? = null

    init {
        gravity = Gravity.CENTER
        orientation = LinearLayout.HORIZONTAL
        initView(context)
    }

    fun setOnEmptyPatientCallback(onEmptyPatientCallback: OnEmptyPatientCallback) {
        mOnEmptyPatientCallback = onEmptyPatientCallback
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.lay_patient_empty_view, this)
        bt_add_patient.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (mOnEmptyPatientCallback != null) {
            mOnEmptyPatientCallback!!.onAddPatientCallback()
        }
    }

    fun show() {
        visibility = View.VISIBLE
    }

    fun hide() {
        visibility = View.GONE
    }

    interface OnEmptyPatientCallback {
        fun onAddPatientCallback()
    }
}
