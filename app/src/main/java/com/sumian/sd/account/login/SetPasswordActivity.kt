package com.sumian.sd.account.login

import android.content.Intent
import android.text.TextUtils
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
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
        btn_confirm.setOnClickListener {
            val password = et_password.getValidText()
            val passwordConfirm = et_password_confirm.getValidText()
            if (!InputCheckUtil.isPasswordValid(password)) {
                InputCheckUtil.toastPasswordInvalidate()
                return@setOnClickListener
            }
            if (!InputCheckUtil.isPasswordValid(passwordConfirm)) {
                InputCheckUtil.toastPasswordInvalidate()
                return@setOnClickListener
            }
            if (!TextUtils.equals(password, passwordConfirm)) {
                ToastUtils.showShort(R.string.password_not_the_same)
                et_password.highlight(true)
                et_password_confirm.highlight(true)
                return@setOnClickListener
            }
            mPresenter!!.setPassword(password!!)
        }
    }

    override fun onSetPasswordSuccess(data: String) {
    }

    override fun onSetPasswordFailed(msg: String) {
    }
}
