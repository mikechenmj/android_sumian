package com.sumian.sd.buz.scale.bean

data class Scale(
        val id: Long,   // scale distribution id
        val doctor_id: Int,
        val scale_result_id: Int,
        val created_at: Int,
        val scale: Scale2,
        val result: ScaleResult,
        val doctor: Doctor?
)

data class ScaleResult(
        val id: Int,
        val score: Int,
        val result: String?,
        val comment: String?,
        val created_at: Int,
        val updated_at: Int
)

data class Scale2(
        val id: Int,
        val doctor_id: Int,
        val score_type: Int,
        val title: String,
        val description: String,
        val final_words: String
)

data class Doctor(
        val id: Int,
        val name: String
)