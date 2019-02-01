package com.sumian.sddoctor.account

import android.text.TextUtils

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/15 11:48
 *     desc   :
 *     version: 1.0
 * </pre>
 */
data class TokenInfo(
        val token: String,
        val expired_at: Int,
        val refresh_expired_at: Int
) {
    fun isExpired(): Boolean {
        return TextUtils.isEmpty(token)
    }
}