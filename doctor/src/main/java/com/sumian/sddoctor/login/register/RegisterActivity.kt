package com.sumian.sddoctor.login.register

import com.sumian.common.base.BaseActivity
import com.sumian.sddoctor.R
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : BaseActivity() {

    private val mVerifyPhoneNumberFragment = RegisterVerifyPhoneNumberFragment()

    override fun getLayoutId(): Int {
        return R.layout.activity_register
    }

    override fun initWidget() {
        super.initWidget()
        title_bar.setOnBackClickListener { onBackPressed() }
    }

    override fun initData() {
        super.initData()
        setTitle(R.string.cellphone_verification)
        supportFragmentManager.beginTransaction()
                .add(R.id.fl_content, mVerifyPhoneNumberFragment)
                .commit()
    }

}
