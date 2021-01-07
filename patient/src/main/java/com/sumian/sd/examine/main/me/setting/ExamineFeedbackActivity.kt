package com.sumian.sd.examine.main.me.setting

import com.sumian.common.base.BaseActivity
import com.sumian.common.helper.ToastHelper
import com.sumian.sd.R
import kotlinx.android.synthetic.main.examine_feedback.*

class ExamineFeedbackActivity : BaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.examine_feedback
    }

    override fun initWidget() {
        super.initWidget()
        bt_submit.setOnClickListener {
            val text = et_feedback.text
            if (text.isBlank()) {
                ToastHelper.show("请填写内容")
                return@setOnClickListener
            }
            et_feedback.setText("")
            ToastHelper.show("已提交，感谢反馈！")
            finish()
        }
    }
}