package com.sumian.sddoctor.login.register

import com.sumian.sddoctor.R
import com.sumian.sddoctor.base.BaseActivity
import kotlinx.android.synthetic.main.activity_register_success.*

class RegisterSuccessActivity : BaseActivity() {
    override fun getContentId(): Int {
        return R.layout.activity_register_success
    }

    override fun initWidget() {
        super.initWidget()
        title_bar.setOnBackClickListener { finish() }
        tv_confirm.setOnClickListener { finish() }
    }

}