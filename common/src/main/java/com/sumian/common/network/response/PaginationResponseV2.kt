package com.sumian.common.network.response

import com.google.gson.annotations.SerializedName

data class PaginationResponseV2<T>(var data: ArrayList<T>, var meta: Meta)

data class Meta(
        @SerializedName("pagination") val pagination: Pagination
)

data class Pagination(
        @SerializedName("total") val total: Int,
        @SerializedName("count") val count: Int,
        @SerializedName("per_page") val perPage: Int,
        @SerializedName("current_page") val currentPage: Int,
        @SerializedName("total_pages") val totalPages: Int
) {
    fun isLastPage(): Boolean {
        return currentPage == totalPages
    }
}