package com.sumian.sd.widget.doctor

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.hyphenate.helpdesk.easeui.UIProvider
import com.sumian.common.image.ImageLoader
import com.sumian.sd.R
import com.sumian.sd.app.App
import com.sumian.sd.doctor.activity.DoctorServiceWebActivity
import com.sumian.sd.doctor.bean.Doctor
import com.sumian.sd.doctor.bean.DoctorService
import com.sumian.sd.kefu.KefuManager
import com.sumian.sd.theme.three.SkinConfig
import com.sumian.sd.widget.dialog.SumianTitleMessageDialog
import com.sumian.sd.widget.refresh.SumianRefreshLayout
import kotlinx.android.synthetic.main.lay_doctor_detail_view.view.*
import java.util.*

/**
 * Created by sm
 * on 2018/5/30 17:36
 * desc:医生详情
 */
class DoctorDetailLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : SumianRefreshLayout(context, attrs) {

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        inflate(context, R.layout.lay_doctor_detail_view, this)
    }

    fun invalidDoctor(doctor: Doctor) {
        ImageLoader.loadImage(doctor.avatar
                ?: "", iv_avatar, R.mipmap.ic_info_avatar_doctor, R.mipmap.ic_info_avatar_doctor)
        tv_name?.text = doctor.name
        tv_department?.text = String.format(Locale.getDefault(), "%s %s", doctor.hospital, doctor.department)
        fold_layout?.setText(doctor.introduction_no_tag)
        siv_customer_service?.setOnClickListener { _ ->
            UIProvider.getInstance().setThemeMode(if (SkinConfig.isInNightMode(App.getAppContext())) 0x02 else 0x01)
            UIProvider.getInstance().clearCacheMsg()
            KefuManager.launchKefuActivity()
        }

        doctor_info?.setOnClickListener { v ->
            SumianTitleMessageDialog(v.context)
                    .showCloseIv(true)
                    .setTitle(v.resources.getString(R.string.doctor_info))
                    .setMessage(doctor.introduction_no_tag!!)
                    .show()
        }

        appendDoctorServices(doctor)
        show()
    }

    private fun appendDoctorServices(doctor: Doctor) {
        if (doctor.services != null && !doctor.services!!.isEmpty()) {
            lay_doctor_service_container?.removeViewsInLayout(2, lay_doctor_service_container.childCount - 2)
            lay_doctor_service_container?.visibility = View.VISIBLE
            var doctorServiceLayout: DoctorServiceLayout
            var doctorService: DoctorService
            val doctorServices = doctor.services
            for (i in doctorServices!!.indices) {
                doctorService = doctorServices[i]
                doctorServiceLayout = DoctorServiceLayout(context)
                doctorServiceLayout.tag = doctorService
                doctorServiceLayout.setOnClickListener { v ->
                    val cacheDoctorService = v.tag as DoctorService
                    DoctorServiceWebActivity.show(context, cacheDoctorService)
                }
                doctorServiceLayout.invalidDoctorService(doctorService, i == doctorServices.size - 1)
                lay_doctor_service_container?.addView(doctorServiceLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            }
            lay_doctor_service_container?.visibility = View.VISIBLE
        } else {
            lay_doctor_service_container?.visibility = View.GONE
        }
    }

    fun show() {
        visibility = View.VISIBLE
    }

    fun hide() {
        visibility = View.GONE
    }

    fun showMsgDot(isHaveMsg: Boolean) {
        siv_customer_service?.setImageResource(if (isHaveMsg) R.drawable.ic_info_customerservice_reply else R.drawable.ic_info_customerservice)
    }
}
