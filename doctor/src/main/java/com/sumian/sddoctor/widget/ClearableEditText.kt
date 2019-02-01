package com.sumian.sddoctor.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.blankj.utilcode.util.ToastUtils
import com.sumian.sddoctor.R
import kotlinx.android.synthetic.main.view_sumian_edittext_with_clear_btn.view.*

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/28 19:33
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class ClearableEditText(context: Context, attributeSet: AttributeSet) : FrameLayout(context, attributeSet) {
    private var mValidator: Validator? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_sumian_edittext_with_clear_btn, this, true)
        iv_clear.setOnClickListener {
            clearEt()
        }
    }

    private fun clearEt() {
        et.setText("")
        et.isActivated = true
        iv_clear.visibility = GONE
    }

    fun getTextWithCheck(): String? {
        if (mValidator == null) {
            throw RuntimeException("Validator is not set yet")
        }
        val text = getText()
        val validate = mValidator?.validate(text) ?: false
        if (validate) {
            return text
        } else {
            et.isActivated = false
            iv_clear.visibility = View.VISIBLE
            ToastUtils.showShort(mValidator?.getInvalidateToastString())
            return null
        }
    }

    private fun getText(): String {
        return et.text.toString()
    }

    interface Validator {
        fun validate(text: String): Boolean
        fun getInvalidateToastString(): String
    }
}