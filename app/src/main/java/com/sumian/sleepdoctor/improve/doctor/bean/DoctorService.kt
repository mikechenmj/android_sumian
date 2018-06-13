package com.sumian.sleepdoctor.improve.doctor.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 *
 *Created by sm
 * on 2018/5/30 10:34
 * desc:
 **/
@Parcelize
data class DoctorService(var id: Int,
                         var name: String,
                         var description: String,
                         var picture: String,//服务 banner
                         var icon: String,//服务 icon
                         var doctor_id: Int,
                         var not_buy_description: String,
                         var bought_description: String,
                         var day_last: Int/*等于0条件：1、未登录，2、未绑定医生，3、未绑定此医生。4、未买过此服务 。5、过期*/,
                         var expired_at: Int/*等于0条件：1、未登录，2、未绑定医生，3、未绑定此医生。4、未买过此服务*/,
                         var last_count: Int/*服务剩余数量*/,
                         var type: Int/*0：睡眠日记，1：图文咨询*/,
                         var remaining_description: String/*剩余描述*/,
                         var packages: ArrayList<DoctorServicePackage> /*参数include=services出现，否则不展示*/
) : Parcelable, Serializable {

    companion object {
        const val SERVICE_TYPE_SLEEP_REPORT: Int = 0x00
        const val SERVICE_TYPE_ADVISORY: Int = 0x01
    }
}