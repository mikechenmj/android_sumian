package com.sumian.device.authentication

import com.google.gson.annotations.SerializedName

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/14 17:57
 * desc   :
 * version: 1.0
 */
data class Token(
        @SerializedName("token")
        var token: String,
        @SerializedName("expired_at")
        var expiredAt: Int,
        @SerializedName("refresh_expired_at")
        var refreshExpiredAt: Int
) {
}