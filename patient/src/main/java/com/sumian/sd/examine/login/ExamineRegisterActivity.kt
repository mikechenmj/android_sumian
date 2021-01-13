package com.sumian.sd.examine.login

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BaseActivity
import com.sumian.common.helper.ToastHelper
import com.sumian.sd.R
import com.sumian.sd.buz.account.login.CheckUtils
import com.sumian.sd.buz.account.login.ImageCaptchaDialogActivity
import com.sumian.sd.buz.account.login.LoginActivity
import com.sumian.sd.common.h5.H5Uri
import com.sumian.sd.common.h5.SimpleWebActivity
import com.sumian.sd.examine.login.bean.RegisterBody
import com.sumian.sd.examine.login.viewmodel.ExamineLoginRouterViewModel
import com.sumian.sd.examine.login.viewmodel.ExamineRegisterViewModel
import kotlinx.android.synthetic.main.activity_login.tv_user_agreement
import kotlinx.android.synthetic.main.activity_login.tv_user_privacy_policy
import kotlinx.android.synthetic.main.examine_activity_main_register.*
import retrofit2.Call

class ExamineRegisterActivity : BaseActivity() {


    private val viewModel by lazy { ViewModelProviders.of(this).get(ExamineRegisterViewModel::class.java) }

    companion object {
        private const val RESULT_CODE_IMAGE_CAPTCHA = 1
        fun show() {
            ActivityUtils.startActivity(ExamineRegisterActivity::class.java)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.examine_activity_main_register
    }

    override fun initWidget() {
        super.initWidget()
        tv_user_agreement.setOnClickListener { SimpleWebActivity.launch(this, H5Uri.USER_AGREEMENT_URL) }
        tv_user_privacy_policy.setOnClickListener { SimpleWebActivity.launch(this, H5Uri.USER_POLICY_URL) }
        tv_captcha.setOnClickListener {
            val mobile = et_mobile.text.toString().trim()
            if (!CheckUtils.isPhoneNum(mobile)) {
                ToastHelper.show("请输入正确的手机号码")
                return@setOnClickListener
            }
            lifecycleScope.launchWhenStarted {
                showLoading()
                val result = viewModel.doCaptcha(mobile)
                dismissLoading()
                when (result) {
                    ExamineRegisterViewModel.IMAGE_CAPTCHA -> {
                        ImageCaptchaDialogActivity.startForResult(this@ExamineRegisterActivity, mobile
                                ?: "", RESULT_CODE_IMAGE_CAPTCHA)
                    }
                    ExamineRegisterViewModel.FAILED -> {
                        ToastHelper.show("获取验证码失败")
                    }
                }
            }
        }
        bt_register.setOnClickListener {
            val mobile = et_mobile.text.toString().trim()
            if (!CheckUtils.isPhoneNum(mobile)) {
                ToastHelper.show("请输入正确的手机号码")
                return@setOnClickListener
            }
            val pwd = et_pwd.text.toString().trim()
            if (!CheckUtils.isValidPassword(pwd)) {
                ToastHelper.show(R.string.pwd_error_hint)
                return@setOnClickListener
            }
            val captcha = et_captcha.text.toString().trim()
            lifecycleScope.launchWhenStarted {
                showLoading()
                viewModel.doRegister(mobile, pwd, captcha)
                dismissLoading()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RESULT_CODE_IMAGE_CAPTCHA -> {
                    if (data == null) {
                        return
                    }
                }
            }
        }
    }
}