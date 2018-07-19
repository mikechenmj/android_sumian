package com.sumian.sleepdoctor.oss.bean

import com.google.gson.annotations.SerializedName

data class OssResponse(
        @SerializedName("access_key_id") val accessKeyId: String,
        @SerializedName("access_key_secret") val accessKeySecret: String,
        @SerializedName("security_token") val securityToken: String,
        @SerializedName("expiration") val expiration: String,
        @SerializedName("bucket") val bucket: String,
        @SerializedName("region") val region: Any,
        @SerializedName("endpoint") val endpoint: String,
        @SerializedName("callback_url") val callbackUrl: String,
        @SerializedName("object") val objectX: String,
        @SerializedName("callback_body") val callbackBody: String
)