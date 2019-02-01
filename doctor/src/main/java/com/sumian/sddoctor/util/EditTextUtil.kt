package com.sumian.sddoctor.util

import android.content.Context
import android.view.View
import android.widget.EditText
import com.blankj.utilcode.util.ToastUtils
import com.sumian.sddoctor.R
import com.sumian.sddoctor.constants.Configs

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/28 19:27
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class EditTextUtil {
    companion object {

        private interface TextValidator {
            fun validate(text: String): Boolean
            fun getInvalidateToastString(): String
        }

        fun getCaptchaWithCheck(context: Context, editText: EditText): String? {
            return getTextWithCheck(editText, object : TextValidator {
                override fun validate(text: String): Boolean {
                    return text.length == Configs.CAPTCHA_LENGTH
                }

                override fun getInvalidateToastString(): String {
                    return context.getString(R.string.captcha_invalidate)
                }
            })
        }


        fun getPasswordWithCheck(context: Context, editText: EditText): String? {
            return getTextWithCheck(editText, object : TextValidator {
                override fun validate(text: String): Boolean {
                    return text.length >= Configs.PASSWORD_LENGTH_MIN && text.length <= Configs.PASSWORD_LENGTH_MAX
                }

                override fun getInvalidateToastString(): String {
                    return context.getString(R.string.password_length_invalidate, Configs.PASSWORD_LENGTH_MIN, Configs.PASSWORD_LENGTH_MAX)
                }
            })
        }

        fun getPhoneNumberWithCheck(context: Context, editText: EditText): String? {
            return getTextWithCheck(editText, object : TextValidator {
                override fun validate(text: String): Boolean {
                    return PhoneNumberUtil.checkMobileValidation(text)
                }

                override fun getInvalidateToastString(): String {
                    return context.getString(R.string.phone_number_invalid)
                }
            })
        }

        private fun getTextWithCheck(editText: EditText, validator: TextValidator): String? {
            val text = editText.text.toString()
            val validate = validator.validate(text)
            return if (validate) {
                text
            } else {
                ToastUtils.showShort(validator.getInvalidateToastString())
                null
            }
        }

        fun errorEditText(editText: EditText, clearView: View, error: Boolean) {
            editText.isActivated = error
            clearView.visibility = if (error) View.VISIBLE else View.GONE
        }

        fun clearEditText(editText: EditText, clearView: View) {
            errorEditText(editText, clearView, false)
            editText.setText("")
        }
    }
}