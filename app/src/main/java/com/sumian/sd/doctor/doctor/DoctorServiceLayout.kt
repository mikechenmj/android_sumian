package com.sumian.sd.doctor.doctor

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import com.sumian.common.image.ImageLoader
import com.sumian.sd.R
import com.sumian.sd.doctor.bean.DoctorService
import kotlinx.android.synthetic.main.lay_item_doctor_service.view.*

/**
 * Created by sm
 * on 2018/5/31 16:50
 * desc:  医生服务容器
 *
 * @author sm
 */
class DoctorServiceLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {


    init {
        initView(context)
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.lay_item_doctor_service, this)
    }

    fun invalidDoctorService(doctorService: DoctorService, isGoneDivider: Boolean) {
        @DrawableRes var serviceIconId = R.mipmap.ic_img_sleepdiary_avatar
        when (doctorService.type) {
            DoctorService.SERVICE_TYPE_ADVISORY -> serviceIconId = R.mipmap.ic_img_advisory_avatar
            DoctorService.SERVICE_TYPE_SLEEP_REPORT -> serviceIconId = R.mipmap.ic_img_sleepdiary_avatar
            DoctorService.SERVICE_TYPE_PHONE_ADVISORY -> serviceIconId = R.mipmap.ic_img_telephone_avatar
            else -> {
            }
        }

        load(doctorService.icon, serviceIconId, iv_service_icon)

        tv_service_name?.text = doctorService.name
        tv_service_desc?.text = doctorService.introduction

        v_divider?.visibility = if (isGoneDivider) View.GONE else View.VISIBLE
    }

    private fun load(url: String, @DrawableRes defaultIconId: Int, iv: ImageView?) {
        ImageLoader.loadImage(url, iv!!, defaultIconId, defaultIconId)
    }
}
