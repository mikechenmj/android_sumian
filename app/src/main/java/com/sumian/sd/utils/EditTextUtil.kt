package com.sumian.sd.utils

import android.text.InputFilter
import android.widget.EditText


/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/20 9:27
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class EditTextUtil {
    companion object {
        fun setMaxLength(editText: EditText, maxLength: Int) {
            editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
        }
    }
}