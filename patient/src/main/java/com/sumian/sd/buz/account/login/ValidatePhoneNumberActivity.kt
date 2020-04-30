package com.sumian.sd.buz.account.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BaseViewModelActivity
import com.sumian.common.statistic.StatUtil
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.stat.StatConstants
import kotlinx.android.synthetic.main.activity_validate_phone_number.*

class ValidatePhoneNumberActivity : BaseViewModelActivity<ValidatePhoneNumberPresenter>(), ValidatePhoneNumberContract.View {
    private var mLaunchType = LAUNCH_TYPE_BIND_SOCIAL
    private var mSocialInfo: String? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_validate_phone_number
    }

    companion object {
        const val KEY_SOCIAL_INFO = "KEY_SOCIAL_INFO"
        const val KEY_LAUNCH_TYPE = "KEY_LAUNCH_TYPE"
        const val LAUNCH_TYPE_BIND_SOCIAL = 1
        const val LAUNCH_TYPE_FORGET_PASSWORD = 2

        private const val RESULT_CODE_IMAGE_CAPTCHA = 1

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
        mViewModel = ValidatePhoneNumberPresenter(this)
    }

    override fun initWidget() {
        super.initWidget()
        title_bar.setOnBackClickListener { onBackPressed() }
        if (mLaunchType == LAUNCH_TYPE_BIND_SOCIAL) {
            StatUtil.event(StatConstants.page_wechat_binding)
        }
        tv_send_captcha.setOnClickListener {
            val mobile = et_mobile.getValidText()
            if (mobile == null) {
                InputCheckUtil.toastPhoneNumberInvalidate()
                return@setOnClickListener
            }
            mViewModel!!.requestCaptcha(mobile)
            val type = if (mLaunchType == LAUNCH_TYPE_BIND_SOCIAL) "绑定微信" else "忘记密码"
            StatUtil.event(StatConstants.click_captcha, mapOf("usage" to type, "mobile" to mobile))
        }
        btn_next.setOnClickListener {
            val mobile = et_mobile.getValidText()
            val captcha = et_captcha.getValidText()
            if (mobile == null) {
                InputCheckUtil.toastPhoneNumberInvalidate()
                return@setOnClickListener
            }
            if (captcha == null) {
                InputCheckUtil.toastCaptchaInvalidate()
                return@setOnClickListener
            }
            when (mLaunchType) {
                LAUNCH_TYPE_BIND_SOCIAL -> mViewModel!!.bindMobile(mobile, captcha, mSocialInfo!!)
                LAUNCH_TYPE_FORGET_PASSWORD -> mViewModel!!.validatePhoneNumberForResetPassword(mobile, captcha)
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

    override fun onRequestCaptchaFail(code: Int) {
        if (code == 4001) {
            showImageCaptcha()
        }
    }

    private fun showImageCaptcha() {
        val mobile = et_mobile.getValidText()
        if (mobile == null) {
            InputCheckUtil.toastPhoneNumberInvalidate()
            return
        }
        ImageCaptchaDialogActivity.startForResult(this, mobile, RESULT_CODE_IMAGE_CAPTCHA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        AppManager.getOpenLogin().delegateActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RESULT_CODE_IMAGE_CAPTCHA -> {
                    if (data == null) {
                        return
                    }
                    onRequestCaptchaSuccess()
                }
            }
        }
    }
}
