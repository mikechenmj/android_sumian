package com.sumian.sddoctor.patient.bean

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import com.google.gson.annotations.SerializedName
import com.sumian.sddoctor.R

data class Patient(var id: Int,
                   var mobile: String?,
                   var name: String?,
                   var nickname: String?,
                   var avatar: String?,
                   var area: String?,
                   var gender: String?,
                   var birthday: String?,
                   var age: String?,
                   var height: String?,
                   var weight: String?,
                   var bmi: String?,
                   var leancloud_id: String?,
                   var doctor_id: Int,
                   var recommend_id: Int,
                   var recommend_type: String,
                   var bound_at: Int,
                   var consulted: Int,
                   var last_login_at: String?,
                   var device_info: String?,
                   var monitor_sn: String?,
                   var sleeper_sn: String?,
                   var career: String?,
                   var education: String?,
                   var tag: Int,
                   var created_at: Int,
                   var updated_at: Int,
                   @SerializedName("real_name") val realName: String?,
                   var weChat: Any?,
                   var set_password: Boolean,
                   val progress_rate: Int,//CBTI 进度查询时使用
                   val start_at: Int,//CBTI 进度查询时使用
                   val cbtis_count: Int
) {


    companion object {

        const val NORMAL_LEVEL = 0 //普通用户
        const val VIP_LEVEL = 1 //VIP
        const val SVIP_LEVEL = 2 //S_VIP

        const val FACED_TYPE = 1 //已面诊
        const val UN_FACED_TYPE = 0 //未面诊

    }

    fun formatTag(): String {
        return when (tag) {
            1 -> {
                "VIP"
            }
            2 -> {
                "SVIP"
            }
            else -> {
                ""
            }
        }
    }

    fun invalidTagView(tagView: ImageView) {
        return when (tag) {
            1 -> {
                tagView.setImageResource(R.drawable.ic_book_details_v)
                tagView.visibility = View.VISIBLE
            }
            2 -> {
                tagView.setImageResource(R.drawable.ic_bookdetails_s)
                tagView.visibility = View.VISIBLE
            }
            else -> {
                tagView.visibility = View.INVISIBLE
            }
        }
    }

    fun formatGender(): String {
        return when (gender) {
            "secrecy" -> {
                "保密"
            }
            "male" -> {
                "男"
            }
            "female" -> {
                "女"
            }
            else -> {
                ""
            }
        }
    }

    fun formatAge(): String {
        return if (!TextUtils.isEmpty(age)) "丨$age" else ""
    }

    fun getNameOrNickname(): String {
        return if (TextUtils.isEmpty(realName)) {
            if (TextUtils.isEmpty(name)) {
                if (TextUtils.isEmpty(nickname)) {
                    ""
                } else {
                    nickname!!
                }
            } else {
                name!!
            }
        } else {
            realName!!
        }
    }

    fun formatCBTIProgress(cbtiProgressType: String): String {
        return when (cbtiProgressType) {
            "passed" -> {
                "已达标"
            }
            "failed" -> {
                "未达标"
            }
            else -> {
                "本周进度：$progress_rate%"
            }
        }
    }
}