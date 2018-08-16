package com.sumian.sd.account.login

import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BasePresenterActivity
import com.sumian.sd.R
import kotlinx.android.synthetic.main.activity_validate_phone_number.*

class ValidatePhoneNumberActivity : BasePresenterActivity<ValidatePhoneNumberContract.Presenter>(), ValidatePhoneNumberContract.View {
    private var mLaunchType = LAUNCH_TYPE_BIND_SOCIAL
    private var mSocialInfo: String? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_validate_phone_number
    }

    companion object {
        const val KEY_SOCIAL_INFO = "KEY_SOCIAL_INFO"
        const val KEY_LAUNCH_TYPE = "KEY_LAUNCH_TYPE"
        const val LAUNCH_TYPE_BIND_SOCIAL = 1
        const val LAUNCH_TYPE_REGISTER = 2
        const val LAUNCH_TYPE_FORGET_PASSWORD = 3

        fun launchForRegister() {
            launch(LAUNCH_TYPE_REGISTER, null)
        }

        fun launchForForgetPassword() {
            launch(LAUNCH_TYPE_FORGET_PASSWORD, null)
        }

        fun launchForBindMobile(socialInfo: String) {
            launch(LAUNCH_TYPE_BIND_SOCIAL, socialInfo)
        }

        private fun launch(type: Int, data: String?) {
            val bundle = Bundle()
            bundle.putInt(KEY_LAUNCH_TYPE, type)
            bundle.putString(KEY_SOCIAL_INFO, data)
            ActivityUtils.startActivity(bundle, ValidatePhoneNumberActivity::class.java)
        }
    }

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        mLaunchType = bundle.getInt(KEY_LAUNCH_TYPE)
        mSocialInfo = bundle.getString(KEY_SOCIAL_INFO)
    }

    override fun initWidgetBefore() {
        super.initWidgetBefore()
        mPresenter = ValidatePhoneNumberPresenter(this)
    }

    override fun initWidget() {
        super.initWidget()
        tv_send_captcha.setOnClickListener {
            val mobile = et_mobile.text.toString()
            mPresenter!!.requestCaptcha(mobile)
            onRequestCaptchaSuccess()
        }
        btn_next.setOnClickListener {
            val mobile = et_mobile.text.toString()
            val captcha = et_captcha.text.toString()
            when (mLaunchType) {
                LAUNCH_TYPE_BIND_SOCIAL -> mPresenter!!.bindMobile(mobile, captcha, mSocialInfo!!)
                LAUNCH_TYPE_REGISTER -> mPresenter!!.validatePhoneNumberForRegister(mobile, captcha)
                LAUNCH_TYPE_FORGET_PASSWORD -> mPresenter!!.validatePhoneNumberForResetPassword(mobile, captcha)
            }
        }
    }

    override fun onValidateSuccess() {
    }

    override fun onValidateFailure() {
    }

    override fun onRequestCaptchaSuccess() {
        tv_send_captcha.startCountDown()
    }
}
