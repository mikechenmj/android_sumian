package com.sumian.sd.account.medal.bean

import com.google.gson.annotations.SerializedName

data class Pagination(
        @SerializedName("count")
        val count: Int,
        @SerializedName("current_page")
        val currentPage: Int,
        @SerializedName("links")
        val links: Any,
        @SerializedName("per_page")
        val perPage: Int,
        @SerializedName("total")
        val total: Int,
        @SerializedName("total_pages")
        val totalPages: Int
)