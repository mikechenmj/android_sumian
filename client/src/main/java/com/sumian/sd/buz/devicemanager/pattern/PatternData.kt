package com.sumian.sd.buz.devicemanager.pattern

import com.google.gson.annotations.SerializedName

/**
 * {
 *  "id":715,
 *  "user_id":2102,
 *  "name":"U1",
 *  "pattern_number":"P0-001",
 *  "value":"10019F2050",
 *  "created_at":1536915321,
 *  "updated_at":1536915321
 * }
 */
data class PatternData(
        @SerializedName("id") val id: Int,
        @SerializedName("user_id") val userId: Int,
        @SerializedName("name") val name: String,
        @SerializedName("pattern_number") val patternNumber: String,
        @SerializedName("value") val value: String, // 1001 9F 20 50
        @SerializedName("created_at") val createdAt: Int,
        @SerializedName("updated_at") val updatedAt: Int
)