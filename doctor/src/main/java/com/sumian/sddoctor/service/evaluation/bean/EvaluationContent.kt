package com.sumian.sddoctor.service.evaluation.bean

import com.google.gson.annotations.SerializedName

/**
 * 周评估报告当中,医生的评估内容  type=0 文字  type=1  语音
 */
data class EvaluationContent(
        @SerializedName("content") val content: String
)