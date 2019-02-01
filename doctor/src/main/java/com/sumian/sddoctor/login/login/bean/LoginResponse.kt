package com.sumian.sddoctor.login.login.bean

import com.sumian.sddoctor.account.TokenInfo

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/14 15:48
 *     desc   :
 *     version: 1.0
 * </pre>
 */

data class LoginResponse(
        val token: String,
        val expired_at: Int,
        val refresh_expired_at: Int,
        val doctor: DoctorInfo,
        val is_new:Boolean
) {
    fun getTokenInfo(): TokenInfo {
        return TokenInfo(token, expired_at, refresh_expired_at)
    }
}