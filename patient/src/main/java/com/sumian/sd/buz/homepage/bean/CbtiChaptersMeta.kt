package com.sumian.sd.buz.homepage.bean

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 *     "meta": {
"is_lock": false,
"unlock_count": 1,
"count": 6,
"total_progress_text": "总进度：第1周/共6周"
}
 */
data class CbtiChaptersMeta(
        @SerializedName("is_lock") val isLock: Boolean,
        @SerializedName("unlock_count") val unlockCount: Int,
        @SerializedName("count") val count: Int,
        @SerializedName("total_progress_text") val totalProgressText: String?,
        @SerializedName("current_status") val currentStatus: String,
        @SerializedName("expired_at") val expiredAt: Int,
        @SerializedName("all_finished") val allFinished: Boolean,
        @SerializedName("joined_count") val joinedCount: Int = 0,
        @SerializedName("final_report") val finalReport: FinalReport?
)

@Parcelize
data class FinalReport(
        @SerializedName("final_online_report_id")
        val finalOnlineReportId: Int, // 0
        @SerializedName("finished_at")
        val finishedAt: Int, // 1546549200
        @SerializedName("scheme")
        val scheme: Scheme
) : Parcelable {
    @Parcelize
    data class Scheme(
            @SerializedName("cbti_id")
            val cbtiId: Int, // 387
            @SerializedName("chapter_id")
            val chapterId: Int, // 6
            @SerializedName("scale_distribution_ids")
            val scaleDistributionIds: String // 9506,9507,9508
    ) : Parcelable
}