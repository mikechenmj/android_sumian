package com.sumian.sd.account.login

import android.content.Intent
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BasePresenterActivity
import com.sumian.sd.R
import kotlinx.android.synthetic.main.activity_set_password.*

class SetPasswordActivity : BasePresenterActivity<SetPasswordContract.Presenter>(), SetPasswordContract.View {
    override fun getLayoutId(): Int {
        return R.layout.activity_set_password
    }

    companion object {
        fun launch() {
            val intent = Intent(ActivityUtils.getTopActivity(), SetPasswordActivity::class.java)
            ActivityUtils.startActivity(intent)
        }
    }

    override fun initWidgetBefore() {
        super.initWidgetBefore()
        mPresenter = SetPasswordPresenter(this)
    }

    override fun initWidget() {
        super.initWidget()
        val password = et_password.text.toString()
        btn_confirm.setOnClickListener { mPresenter!!.setPassword(password) }
    }

    override fun onSetPasswordSuccess(data: String) {
    }

    override fun onSetPasswordFailed(msg: String) {
    }
}
