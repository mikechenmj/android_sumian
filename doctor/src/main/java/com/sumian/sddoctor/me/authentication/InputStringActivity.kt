package com.sumian.sddoctor.me.authentication

import android.app.Activity
import android.content.Intent
import android.text.Editable
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.utils.ColorCompatUtil
import com.sumian.common.utils.InputCheckUtil
import com.sumian.common.widget.adapter.EmptyTextWatcher
import com.sumian.sddoctor.R
import com.sumian.sddoctor.base.SddBaseActivity
import kotlinx.android.synthetic.main.layout_label_edittext.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/11/23 17:04
 * desc   :
 * version: 1.0
 */
class InputStringActivity : SddBaseActivity() {
    private lateinit var mTitle: String
    private lateinit var mMenu: String
    private lateinit var mLabel: String
    private lateinit var mFieldName: String
    private lateinit var mInputHint: String
    private var mInputMinLength: Int = 0
    private var mInputMaxLength: Int = 0

    override fun getLayoutId(): Int {
        return R.layout.activity_add_string
    }

    companion object {
        const val KEY_TITLE = "KEY_TITLE"
        const val KEY_MENU = "KEY_MENU"
        const val KEY_LABEL = "KEY_LABEL"
        const val KEY_FIELD_NAME = "KEY_FIELD_NAME"
        const val KEY_INPUT_HINT = "KEY_INPUT_HINT"
        const val KEY_INPUT_MIN_LENGTH = "KEY_INPUT_MIN_LENGTH"
        const val KEY_INPUT_MAX_LENGTH = "KEY_INPUT_MAX_LENGTH"
        private const val KEY_INPUT_VALUE = "KEY_INPUT_VALUE"

        fun launchForResult(activity: Activity,
                            requestCode: Int,
                            title: String,
                            menu: String,
                            label: String,
                            fieldName: String,
                            inputHint: String,
                            inputMinLength: Int,
                            inputMaxLength: Int) {
            val intent = Intent(ActivityUtils.getTopActivity(), InputStringActivity::class.java)
            intent.putExtra(KEY_TITLE, title)
            intent.putExtra(KEY_MENU, menu)
            intent.putExtra(KEY_LABEL, label)
            intent.putExtra(KEY_FIELD_NAME, fieldName)
            intent.putExtra(KEY_INPUT_HINT, inputHint)
            intent.putExtra(KEY_INPUT_MIN_LENGTH, inputMinLength)
            intent.putExtra(KEY_INPUT_MAX_LENGTH, inputMaxLength)
            ActivityUtils.startActivityForResult(activity, intent, requestCode)
        }

        fun getString(intent: Intent?): String? {
            return intent?.getStringExtra(KEY_INPUT_VALUE)
        }

    }

    override fun initWidget() {
        super.initWidget()
        mTitle = intent.getStringExtra(KEY_TITLE)
        mMenu = intent.getStringExtra(KEY_MENU)
        mLabel = intent.getStringExtra(KEY_LABEL)
        mFieldName = intent.getStringExtra(KEY_FIELD_NAME)
        mInputHint = intent.getStringExtra(KEY_INPUT_HINT)
        mInputMinLength = intent.getIntExtra(KEY_INPUT_MIN_LENGTH, 0)
        mInputMaxLength = intent.getIntExtra(KEY_INPUT_MAX_LENGTH, 0)
        setTitle(mTitle)
        mTitleBar.setMenuText(mMenu)
        setMenuEnable(false)
        tv_label.text = mLabel
        et.hint = mInputHint

        et.addTextChangedListener(object : EmptyTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                super.afterTextChanged(s)
                setMenuEnable(et.text.toString().isNotEmpty())
            }
        })
    }

    private fun setMenuEnable(enable: Boolean) {
        mTitleBar.mTvMenu.setTextColor(ColorCompatUtil.getColor(this@InputStringActivity, if (enable) R.color.b3_color else R.color.t2_color))
        mTitleBar.setOnMenuClickListener { if (enable) onConfirmClick() }
    }

    override fun showBackNav(): Boolean {
        return true
    }

    private fun onConfirmClick() {
        val isValid = InputCheckUtil.checkInput(et, mFieldName, mInputMinLength, mInputMaxLength)
        if (isValid) {
            val input = et.text.toString()
            val intent = Intent()
            intent.putExtra(KEY_INPUT_VALUE, input)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}