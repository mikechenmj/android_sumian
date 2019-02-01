package com.sumian.sddoctor.service.cbti.bean

import com.google.gson.annotations.SerializedName
import com.sumian.sddoctor.service.cbti.bean.CbtiChapterData
import com.sumian.sddoctor.service.cbti.bean.CbtiChaptersMeta

data class GetCbtiChaptersResponse(
        @SerializedName("data") val data: List<CbtiChapterData>,
        @SerializedName("meta") val meta: CbtiChaptersMeta
)