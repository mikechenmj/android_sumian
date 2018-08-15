package com.sumian.sd.homepage.bean

import com.google.gson.annotations.SerializedName

data class GetCbtiChaptersResponse(
        @SerializedName("data") val data: List<CbtiChapterData>,
        @SerializedName("meta") val meta: CbtiChaptersMeta
)