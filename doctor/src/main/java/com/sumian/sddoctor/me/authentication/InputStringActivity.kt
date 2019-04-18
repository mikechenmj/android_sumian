package com.sumian.sddoctor.me.authentication

import android.app.Activity
import android.content.Intent
import android.os.Parcelable
import android.text.Editable
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.utils.ColorCompatUtil
import com.sumian.common.utils.InputCheckUtil
import com.sumian.common.widget.adapter.EmptyTextWatcher
import com.sumian.sddoctor.R
import com.sumian.sddoctor.base.SddBaseActivity
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.layout_label_edittext.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/11/23 17:04
 * desc   :
 * version: 1.0
 */
class InputStringActivity : SddBaseActivity() {
    private lateinit var mParams: InputStringParams

    override fun getLayoutId(): Int {
        return R.layout.activity_add_string
    }

    companion object {
        private const val KEY_INPUT_VALUE = "KEY_INPUT_VALUE"
        private const val KEY_PARAMS = "KEY_PARAMS"

        fun launchForResult(activity: Activity,
                            requestCode: Int,
                            params: InputStringParams) {
            val intent = Intent(ActivityUtils.getTopActivity(), InputStringActivity::class.java)
            intent.putExtra(KEY_PARAMS, params)
            ActivityUtils.startActivityForResult(activity, intent, requestCode)
        }

        fun getString(intent: Intent?): String? {
            return intent?.getStringExtra(KEY_INPUT_VALUE)
        }
    }

    @Parcelize
    data class InputStringParams(
            val title: String,
            val menu: String,
            val label: String,
            val fieldName: String,
            val inputHint: String,
            val inputMinLength: Int,
            val inputMaxLength: Int
    ) : Parcelable

    override fun initWidget() {
        super.initWidget()
        mParams = intent.getParcelableExtra(KEY_PARAMS)
        setTitle(mParams.title)
        mTitleBar.setMenuText(mParams.menu)
        setMenuEnable(false)
        tv_label.text = mParams.label
        et.hint = mParams.inputHint

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
        val isValid = InputCheckUtil.checkInput(et, mParams.fieldName, mParams.inputMinLength, mParams.inputMaxLength)
        if (isValid) {
            val input = et.text.toString()
            val intent = Intent()
            intent.putExtra(KEY_INPUT_VALUE, input)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}