package com.sumian.sddoctor.widget.text

import android.widget.EditText

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/25 11:36
 * desc   :
 * version: 1.0
 */
open class MoneyTextWatcher(editText: EditText) : EmptyTextWatcher() {
    private val mEditText = editText
    private val digits = 2

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        val s1 = s.toString().trim()
        var s2: CharSequence = ""
        //删除“.”后面超过2位后的数据
        if (s1.contains(".")) {
            if (s1.length - s1.indexOf(".") > digits + 1) {
                s2 = s1.subSequence(0, s1.length - 1)
                mEditText.setText(s2)
                mEditText.setSelection(s2.length) //光标移到最后
            }
        }
        //如果"."在起始位置,则起始位置自动补0
        if (s1.startsWith(".")) {
            s2 = "0$s"
            mEditText.setText(s2)
            mEditText.setSelection(2)
        }

        //如果起始位置为0,且第二位跟的不是".",则无法后续输入
        if (s1.startsWith("0") && s1.length > 1) {
            if (s1.substring(1, 2) != ".") {
                mEditText.setText(s?.subSequence(0, 1))
                mEditText.setSelection(1)
            }
        }
    }

}