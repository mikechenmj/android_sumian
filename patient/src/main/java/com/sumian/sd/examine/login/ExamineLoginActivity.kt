package com.sumian.sd.examine.login

import android.text.TextUtils
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.sumian.common.base.BaseActivity
import com.sumian.common.helper.ToastHelper
import com.sumian.sd.R
import com.sumian.sd.examine.login.viewmodel.ExamineLoginViewModel
import kotlinx.android.synthetic.main.activity_examine_login.*

class ExamineLoginActivity : BaseActivity() {

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(ExamineLoginViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_examine_login
    }

    override fun initWidget() {
        super.initWidget()
        bt_login.setOnClickListener {
            lifecycleScope.launchWhenStarted {
                val mobile: String = et_mobile.text.toString().trim { it <= ' ' }

                if (TextUtils.isEmpty(mobile)) {
                    ToastHelper.show(R.string.phone_number_invalid_toast)
                    return@launchWhenStarted
                }

                val pwd: String = et_pwd.text.toString().trim { it <= ' ' }

                if (TextUtils.isEmpty(pwd)) {
                    ToastHelper.show(R.string.pwd_error_hint)
                    return@launchWhenStarted
                }
                showLoading()
                viewModel.loginByPassword(mobile, pwd)
                dismissLoading()
            }
        }
    }
}