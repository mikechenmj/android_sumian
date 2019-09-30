package com.sumian.sd.buz.scale.bean

data class FilledScaleCollection(
        val id: Long,
        val title: String,
        val distributions: ArrayList<CollectionDistributions>
) {
    data class CollectionDistributions(
            val id: Long,
            val updated_at: Long,
            var collectionId: Long,
            var title: String
    ) {
        fun getUpdateAtInMillis(): Long {
            return updated_at * 1000L
        }
    }
}