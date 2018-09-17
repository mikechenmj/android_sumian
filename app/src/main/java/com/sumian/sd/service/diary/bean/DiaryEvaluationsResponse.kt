package com.sumian.sd.service.diary.bean

import com.google.gson.annotations.SerializedName

data class DiaryEvaluationsResponse(
        @SerializedName("data") val data: List<DiaryEvaluationData>,
        @SerializedName("meta") val meta: Meta
)