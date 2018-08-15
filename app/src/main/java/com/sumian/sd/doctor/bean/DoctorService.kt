package com.sumian.sd.doctor.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *
 *Created by sm
 * on 2018/5/30 10:34
 * desc:
 **/
@Parcelize
data class DoctorService(var id: Int,
                         var type: Int/*0：睡眠日记，1：图文咨询*/,
                         var name: String,//服务名
                         var description: String, //服务描述
                         var introduction: String, //服务简介
                         var banner_type: Int,//展示位类型 0：图片 1：视频
                         var picture: String,//图片 url banner_type=0
                         var video: String,//视频 url banner_type=1
                         var icon: String,//图标 url
                         var service_packages: ArrayList<DoctorServicePackage> /*参数include=services出现，否则不展示*/
) : Parcelable {

    companion object {
        const val SERVICE_TYPE_SLEEP_REPORT: Int = 0
        const val SERVICE_TYPE_ADVISORY: Int = 1
        const val SERVICE_TYPE_PHONE_ADVISORY: Int = 2
        const val SERVICE_TYPE_CBTI: Int = 3
    }
}