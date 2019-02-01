package com.sumian.sddoctor.service.advisory.bean

import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import com.sumian.sddoctor.R
import com.sumian.sddoctor.patient.bean.Patient
import com.sumian.sddoctor.util.ResUtils

/**
 *
 *Created by sm
 * on 2018/6/4 14:51
 * desc:  图文咨询详情,内容包括,1.患者信息 2.医生信息 3.内容(语音,文字,图片,报告)
 **/
data class Advisory(var id: Int, //咨询 ID
                    var doctor_id: Int,//提供咨询服务的医生 id
                    var traceable_id: Int,//根据不同的图文咨询类型，去不同接口请求的id
                    var traceable_type: String,//图文资讯类型  App\\Models\\OperatorAdvisory 客服图文资讯 App\\Models\\Advisory 用户图文资讯
                    var status: Int,//状态 0：未回复 1：已回复 2：已完成 3：已关闭 4：已取消，跟traceable里状态一致
                    var last_asked_at: Int,//最后提问时间
                    var created_at: Int,//咨询服务购买时间,unix 时间戳
                    var updated_at: Int, //最后更新时间 最后一条记录时间
                    var traceable: Traceable//咨询 include=traceable   type=App\\Models\\OperatorAdvisory
) {
    companion object {

        const val ALL_TYPE: Int = 0xff    //全部状态
        const val REPLYING_TYPE: Int = 0x00  //待回复
        const val REPLIED_TYPE: Int = 0x01    //已回复
        const val FINISHED_TYPE: Int = 0x02    //已完成
        const val CLOSED_TYPE: Int = 0x03    //已关闭
        const val CANCEL_TYPE: Int = 0x04   //已取消

    }

    fun formatStatus(): CharSequence {
        return when (status) {
            0 -> {
                val formatStatus = "待回复"
                val spannableString = SpannableString(formatStatus)
                spannableString.setSpan(ForegroundColorSpan(ResUtils.getColor(R.color.b3_color)), 0, formatStatus.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                return spannableString
            }
            1 -> {
                "已回复"
            }
            2 -> {
                "已完成"
            }
            3 -> {
                "已关闭"
            }
            4 -> {
                "已取消"
            }
            else -> {//5 待提问(待使用)  患者才有的状态
                ""
            }
        }

    }

    data class Traceable(var id: Int,
                         var status: Int,//状态 0：待回复 1：已回复 2：已完成 3：已关闭 4：已取消
                         var package_id: Int,//服务包 id
                         var order_id: Int,//订单 id
                         var user_id: Int,//用户 id
                         var doctor_id: Int,//提供咨询服务的医生 id
                         var questioner_id: Int,//客服图文资讯才有的值 客服图文资讯的提问者
                         var questioner_type: String,//客服图文资讯才有的值 客服图文资讯的提问者身份
                         var start_at: Int,//开始提问时间，unix 时间戳
                         var end_at: Int,//结束提问时间，unix 时间戳
                         var created_at: Int,//咨询服务购买时间,unix 时间戳
                         var updated_at: Int,//最后更新时间,unix 时间戳
                         var second_last: Int, //无用 剩余时间
                         var first_content: String,//第一条提问内容
                         var last_content: String,//最后一条提问内容
                         var records: ArrayList<Record>,//提问回答记录 include=traceable.records
                         var user: Patient,//用户信息 include=traceable.user
                         var doctor: Doctor//医生信息 include=traceable.doctor
    )
}
