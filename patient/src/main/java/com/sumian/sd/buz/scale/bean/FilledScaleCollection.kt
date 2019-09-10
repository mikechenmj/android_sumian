package com.sumian.sd.buz.scale.bean

data class FilledScaleCollection(
        val id: Long,
        val title: String,
        val distributions: CollectionDistributions
) {
    data class CollectionDistributions(
            val id: Long,
            val updated_at: Long
    ) {
        fun getUpdateAtInMillis(): Long {
            return updated_at * 1000L
        }
    }
}