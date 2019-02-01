package com.sumian.sddoctor.login.register.bean

import com.google.gson.annotations.SerializedName


/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/28 17:18
 *     desc   :
 *     version: 1.0
 * </pre>
 */

data class ValidateRegisterCaptchaResponse(
        @SerializedName("ticket") val ticket: String,
        @SerializedName("retailer_invitation_code") val inviteCode: String
)