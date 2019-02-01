package com.sumian.sddoctor.homepage.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.cardview.widget.CardView
import com.sumian.sddoctor.R
import kotlinx.android.synthetic.main.lay_doctor_services_view.view.*

@Suppress("DEPRECATION")
/**
 * Created by sm
 *
 * on 2018/8/27
 *
 * desc:  首页,医生服务输入口
 *
 */
class DoctorServicesView : CardView, View.OnClickListener {

    private var mOnDoctorServicesCallback: OnDoctorServicesCallback? = null


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setBackgroundResource(R.drawable.ic_home_bg)
        cardElevation = context.resources.getDimension(R.dimen.space_4)
        radius = context.resources.getDimension(R.dimen.space_4)
        preventCornerOverlap = true
        addView(context)
    }

    private fun addView(context: Context) {
        View.inflate(context, R.layout.lay_doctor_services_view, this)
        tv_booking.setOnClickListener(this)
        tv_cbti_progress.setOnClickListener(this)
        tv_doc_advisory.setOnClickListener(this)
        tv_note_evaluate.setOnClickListener(this)
    }

    fun setOnDoctorServicesCallback(onDoctorServicesCallback: OnDoctorServicesCallback) {
        this.mOnDoctorServicesCallback = onDoctorServicesCallback
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_booking -> {
                mOnDoctorServicesCallback?.onShowBookingService(v)
            }
            R.id.tv_cbti_progress -> {
                mOnDoctorServicesCallback?.onShowCBTIService(v)
            }
            R.id.tv_doc_advisory -> {
                mOnDoctorServicesCallback?.onShowAdvisoryService(v)
            }
            R.id.tv_note_evaluate -> {
                mOnDoctorServicesCallback?.onShowEvaluateService(v)
            }
        }
    }


    interface OnDoctorServicesCallback {

        fun onShowBookingService(v: View)

        fun onShowCBTIService(v: View)

        fun onShowAdvisoryService(v: View)

        fun onShowEvaluateService(v: View)

    }
}