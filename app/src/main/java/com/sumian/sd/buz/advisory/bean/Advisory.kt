package com.sumian.sd.buz.advisory.bean

import android.os.Parcelable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import com.sumian.sd.R
import com.sumian.sd.app.App
import com.sumian.sd.buz.account.bean.UserInfo
import com.sumian.sd.buz.doctor.bean.Doctor
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 *
 *Created by sm
 * on 2018/6/4 14:51
 * desc:  咨询详情
 **/
@Suppress("DEPRECATION")
@Parcelize
data class Advisory(var id: Int, //咨询 ID
                    var status: Int,//咨询状态 0: 待回复 1：已回复 2：已结束 3：已关闭，4：已取消，5：待提问(待使用)
                    var package_id: Int,//服务包 id
                    var order_id: Int,//订单 id
                    var user_id: Int,//用户 id
                    var doctor_id: Int,//提供咨询服务的医生 id
                    var start_at: Int,//开始提问时间，unix 时间戳
                    var end_at: Int,//结束提问时间，unix 时间戳
                    var created_at: Int,//咨询服务购买时间,unix 时间戳
                    var updated_at: Int,//最后更新时间,unix 时间戳
                    var description: String,//描述
                    var service_id: Int,//服务 ID
                    var remind_description: String,//顶部提示条文案
                    var last_count: Int,//剩余提问次数
                    var last_second: Int?,//离结束时间剩余秒 如果没有开始为null 如果已经结束为0
                    var records: ArrayList<Record>?,//提问或回复记录
                    var user: UserInfo?,//用户信息
                    var doctor: Doctor?//医生信息
) : Parcelable, Serializable {
    companion object {

        const val UNFINISHED_TYPE: Int = 0x00  //未完成的咨询
        const val FINISHED_TYPE: Int = 0x01    //已完成的咨询
    }

    fun formatStatus(): CharSequence {
        return when (status) {
            0 -> {
                val formatStatus = "待回复"
                val spannableString = SpannableString(formatStatus)
                spannableString.setSpan(ForegroundColorSpan(App.getAppContext().resources.getColor(R.color.b3_color)), 0, formatStatus.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                return spannableString
            }
            1 -> {
                "已回复"
            }
            2, 4 -> {
                "已完成"
            }
            3 -> {
                "已关闭"
            }
            else -> {//5 待提问(待使用)
                ""
            }
        }

    }
}
