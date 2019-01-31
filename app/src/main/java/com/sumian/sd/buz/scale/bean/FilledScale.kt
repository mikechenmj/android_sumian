package com.sumian.sd.buz.scale.bean

data class FilledScale(
        val id: Long,
        val title: String,
        val score_type: Int,
        val latest_scale_distribution: LatestScaleDistribution
)

data class LatestScaleDistribution(
        val id: Long,
        val scale_id: Int,
        val user_id: Int,
        val doctor_id: Int,
        val scale_result_id: Int,
        val created_at: Int,
        val updated_at: Int,
        val result: Result
) {
    fun getUpdateAtInMillis(): Long {
        return updated_at * 1000L
    }
}

data class Result(
        val id: Int,
        val scale_id: Int,
        val user_id: Int,
        val score: Int,
        val result: String,
        val comment: String,
        val created_at: Int,
        val updated_at: Int
)