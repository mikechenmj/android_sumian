package com.sumian.sddoctor.patient.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.sumian.sddoctor.R

/**
 * Created by sm
 *
 * on 2018/8/29
 *
 * desc:  新患者列表 view
 *
 */
class PatientListEmptyView : LinearLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        orientation = LinearLayout.VERTICAL
        initView(context)
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.lay_patient_list_empty_view, this)
    }


    fun show() {
        visibility = View.VISIBLE
    }

    fun hide() {
        visibility = View.GONE
    }
}