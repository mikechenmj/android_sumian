package com.sumian.sddoctor.login.register

import com.sumian.common.base.BaseActivity
import com.sumian.sddoctor.R
import kotlinx.android.synthetic.main.activity_register_success.*

class RegisterSuccessActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_register_success
    }

    override fun initWidget() {
        super.initWidget()
        title_bar.setOnBackClickListener { finish() }
        tv_confirm.setOnClickListener { finish() }
    }

}