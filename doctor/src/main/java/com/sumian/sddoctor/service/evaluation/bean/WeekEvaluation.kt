package com.sumian.sddoctor.service.evaluation.bean

import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import com.google.gson.annotations.SerializedName
import com.sumian.sddoctor.R
import com.sumian.sddoctor.patient.bean.Patient
import com.sumian.sddoctor.service.advisory.bean.Doctor
import com.sumian.sddoctor.util.ResUtils

/**
 * 周评估
 */
data class WeekEvaluation(

        @SerializedName("id") val id: Int,
        @SerializedName("user_id") val userId: Int,
        @SerializedName("doctor_id") val doctorId: Int,
        @SerializedName("package_id") val packageId: Int,
        @SerializedName("traceable_id") val traceableId: Int,
        @SerializedName("traceable_type") val traceableType: String,
        @SerializedName("status") val status: Int,
        @SerializedName("start_at") val startAt: Int,
        @SerializedName("end_at") val endAt: Int,
        @SerializedName("diary_start_at") val diaryStartAt: Int,
        @SerializedName("diary_end_at") val diaryEndAt: Int,
        @SerializedName("remark") val remark: String,
        @SerializedName("evaluation_type") val evaluationType: Int,
        @SerializedName("evaluation_content") val evaluationContent: EvaluationContent?,
        @SerializedName("evaluated_at") val evaluatedAt: Int,
        @SerializedName("diaries") val diaries: List<Diary>,
        @SerializedName("created_at") val createdAt: Int,
        @SerializedName("updated_at") val updatedAt: Int,
        @SerializedName("description") val description: String,
        @SerializedName("second_last") val secondLast: Int,
        @SerializedName("user") val user: Patient,
        @SerializedName("doctor") val doctor: Doctor
) {

    companion object {

        const val ALL_TYPE: String = "all_used"    //全部状态
        const val REPLYING_TYPE: String = "not_reply"  //待回复
        const val FINISHED_TYPE: String = "finished"    //已完成
        const val CLOSED_TYPE: String = "closed"    //已关闭
        const val CANCEL_TYPE: String = "canceled"  //已取消

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
                "已完成"
            }
            2 -> {
                "已关闭"
            }
            3 -> {
                "已取消"
            }
            else -> {//5 待提问(待使用)  患者才有的状态
                ""
            }
        }

    }
}