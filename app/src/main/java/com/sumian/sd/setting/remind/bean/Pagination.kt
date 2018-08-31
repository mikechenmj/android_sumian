package com.sumian.sd.setting.remind.bean

import com.google.gson.annotations.SerializedName

data class Pagination(
        @SerializedName("total") val total: Int,
        @SerializedName("count") val count: Int,
        @SerializedName("per_page") val perPage: Int,
        @SerializedName("current_page") val currentPage: Int,
        @SerializedName("total_pages") val totalPages: Int,
        @SerializedName("links") val links: List<Any>
)