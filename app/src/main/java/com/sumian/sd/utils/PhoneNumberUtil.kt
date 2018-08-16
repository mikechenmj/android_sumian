package com.sumian.sd.utils

import android.text.TextUtils

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/14 20:38
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class PhoneNumberUtil {
    companion object {
        fun checkMobileValidation(mobile: String): Boolean {
            return !TextUtils.isEmpty(mobile) && mobile.matches("^1([0-9]{10})$".toRegex())
        }
    }
}