package com.sumian.sd.advisory.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *
 *Created by sm
 * on 2018/6/4 14:59
 * desc:在线报告
 **/
@Parcelize
data class Report(var id: Int,//在线报告 ID
                  var title: String,//报告标题
                  var mobile: String,//用户手机
                  var user_name: String,//用户姓名
                  var doctor_id: Int,//医生 ID
                  var user_id: Int,//用户 ID
                  var report_url: String//在线报告 URL
) : Parcelable