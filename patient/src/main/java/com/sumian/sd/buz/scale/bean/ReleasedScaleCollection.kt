package com.sumian.sd.buz.scale.bean

data class ReleasedScaleCollection(
        val id: Long,
        val doctor_id: Long,
        val created_at: Long,
        val title: String,
        val scales: ArrayList<Scale>
) {
    data class Scale(
            val id: Long,
            val doctor_id: Long,
            val score_type: Long,
            val title: String,
            val description: String,
            val final_words: String
    )
}