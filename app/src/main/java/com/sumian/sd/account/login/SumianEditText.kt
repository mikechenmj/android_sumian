package com.sumian.sd.account.login

import android.content.Context
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.sumian.sd.R
import kotlinx.android.synthetic.main.sumain_edit_text.view.*

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/17 10:25
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class SumianEditText(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet) {
    private var mValidateRegex: String? = null
    private var mStateChangeListener: StateChangeListener? = null

    interface StateChangeListener {
        fun onHighlightChange(highlight: Boolean)
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.sumain_edit_text, this, true)
        val a = context.obtainStyledAttributes(attributeSet, R.styleable.SumianEditText)
        val text = a.getString(R.styleable.SumianEditText_set_text)
        val hint = a.getString(R.styleable.SumianEditText_set_hint)
        val inputType = a.getInt(R.styleable.SumianEditText_set_input_type, 0)
        val showBg = a.getBoolean(R.styleable.SumianEditText_set_show_bg, true)
        mValidateRegex = a.getString(R.styleable.SumianEditText_set_validation_regex)
        a.recycle()
        if (showBg) {
            setBackgroundResource(R.drawable.hw_et_bg_sel)
        }
        et.setText(text)
        et.hint = hint
        when (inputType) {
            0 -> et.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            1 -> et.inputType = InputType.TYPE_CLASS_NUMBER
            2 -> {
                et.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                et.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
        iv_clear.setOnClickListener {
            et.setText("")
            highlight(false)
        }
    }

    fun setValidateRegex(validateRegex: String?) {
        mValidateRegex = validateRegex
    }

    /**
     * return origin text
     */
    fun getText(): String {
        return et.text.toString()
    }

    /**
     * return valid text or null
     */
    fun getValidText(): String? {
        return if (checkTextValidation()) getText() else null
    }

    fun checkTextValidation(): Boolean {
        val text = getText()
        val isInputValid = mValidateRegex == null || text.matches(mValidateRegex!!.toRegex())
        highlight(!isInputValid)
        return isInputValid
    }

    fun highlight(highlight: Boolean) {
        isActivated = highlight
        iv_clear.visibility = if (!highlight) GONE else VISIBLE
        mStateChangeListener?.onHighlightChange(highlight)
    }

    fun setStateChangeListener(stateChangedListener: StateChangeListener) {
        mStateChangeListener = stateChangedListener
    }

}