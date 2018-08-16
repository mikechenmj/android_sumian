package com.sumian.sd.account.login

import com.sumian.common.base.BasePresenterActivity
import com.sumian.sd.R

class SetPasswordActivity : BasePresenterActivity<SetPasswordContract.Presenter>(), SetPasswordContract.View {
    override fun getLayoutId(): Int {
        return R.layout.activity_set_password
    }

    override fun onSetPasswordSuccess(data: String) {
    }

    override fun onSetPasswordFailed(msg: String) {
    }
}
