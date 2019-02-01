package com.sumian.sddoctor.util

import android.text.TextUtils
import android.widget.EditText
import com.blankj.utilcode.util.ToastUtils
import com.sumian.sddoctor.R

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/11/20 16:24
 * desc   :
 * version: 1.0
 */
class InputCheckUtil {
    companion object {
        fun checkInput(et: EditText, fieldName: String, minLength: Int, maxLength: Int): Boolean {
            return checkInput(et.text.toString(), fieldName, minLength, maxLength)
        }

        @Suppress("MemberVisibilityCanBePrivate")
        fun checkInput(text: String, fieldName: String, minLength: Int, maxLength: Int): Boolean {
            val length = text.length
            if (minLength != 0 && TextUtils.isEmpty(text)) {
                ToastUtils.showShort(R.string.content_can_not_be_empty, fieldName)
                return false
            }
            if (length < minLength) {
                ToastUtils.showShort(R.string.content_is_too_short, fieldName, minLength)
                return false
            }
            if (length > maxLength) {
                ToastUtils.showShort(R.string.content_is_too_long, fieldName, maxLength)
                return false
            }
            return true
        }

    }
}