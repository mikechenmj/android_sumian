package com.sumian.sd.homepage.bean

import com.google.gson.annotations.SerializedName

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
        @SerializedName("total_progress_text") val totalProgressText: String,
        @SerializedName("current_status") val currentStatus: String
)