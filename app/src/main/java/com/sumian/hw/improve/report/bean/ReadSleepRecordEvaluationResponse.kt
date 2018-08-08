package com.sumian.hw.improve.report.bean

import com.google.gson.annotations.SerializedName

data class ReadSleepRecordEvaluationResponse(
        @SerializedName("has_unread_evaluation") val hasUnreadEvaluation: Boolean
)