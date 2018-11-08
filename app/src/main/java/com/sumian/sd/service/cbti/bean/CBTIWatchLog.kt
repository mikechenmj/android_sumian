package com.sumian.sd.service.cbti.bean

import com.google.gson.annotations.SerializedName

data class CBTIWatchLog(
        @SerializedName("id")
        val id: Int,
        @SerializedName("cbti_id")
        val cbtiId: Int,
        @SerializedName("cbti_course_id")
        val cbtiCourseId: Int,
        @SerializedName("created_at")
        val createdAt: Int,
        @SerializedName("end_point")
        val endPoint: Int,
        @SerializedName("finished_at")
        val finishedAt: Int,
        @SerializedName("total_watch_length")
        val totalWatchLength: Int,
        @SerializedName("updated_at")
        val updatedAt: Int,
        @SerializedName("video_progress")
        val videoProgress: String,
        @SerializedName("watch_times")
        val watchTimes: Int
)