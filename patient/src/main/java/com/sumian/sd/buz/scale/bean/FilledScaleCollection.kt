package com.sumian.sd.buz.scale.bean

import com.google.gson.annotations.Expose

data class FilledScaleCollection(
        val id: Long,
        val title: String,
        val distributions: ArrayList<CollectionDistributions>
) {
    data class CollectionDistributions(
            val id: Long,
            val updated_at: Long,
            @Expose var collectionId: Long,
            @Expose var title: String
    ) {
        fun getUpdateAtInMillis(): Long {
            return updated_at * 1000L
        }
    }
}