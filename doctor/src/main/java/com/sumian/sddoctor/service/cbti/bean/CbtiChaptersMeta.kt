package com.sumian.sddoctor.service.cbti.bean

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
        @SerializedName("total_videos") val totalVideos: Int,//视频总数
        @SerializedName("finished_videos") val finishedVideos: Int,//完成视频数
        @SerializedName("finished_percent") val finishedPercent: Int//完成百分比
)