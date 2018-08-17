package com.sumian.sd.account.login

import com.blankj.utilcode.util.ToastUtils
import com.sumian.sd.R
import com.sumian.sd.account.config.SumianConfig

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/17 9:12
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class InputCheckUtil {
    companion object {
        fun isPhoneNumberValid(phoneNumber: String?): Boolean {
            return phoneNumber != null && phoneNumber.matches("^1([0-9]{10})$".toRegex())
        }

        fun isPasswordValid(password: String?): Boolean {
            val regex = String.format(".{%d,%d}", SumianConfig.PASSWORD_LENGTH_MIN, SumianConfig.PASSWORD_LENGTH_MAX)
            return password != null && password.matches(regex.toRegex())
        }

        fun isCaptchaValid(captcha: String?): Boolean {
            return captcha != null && captcha.length == SumianConfig.CAPTCHA_LENGTH
        }

        fun toastPhoneNumberInvalidate() {
            ToastUtils.showShort(R.string.phone_number_invalid_toast)
        }

        fun toastCaptchaInvalidate() {
            ToastUtils.showShort(R.string.captcha_invalid_toast)
        }

        fun toastPasswordInvalidate() {
            ToastUtils.showShort(R.string.password_invalid_toast, SumianConfig.PASSWORD_LENGTH_MIN, SumianConfig.PASSWORD_LENGTH_MAX)
        }
    }
}