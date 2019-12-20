package com.sumian.sddoctor.login.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseActivity
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.constants.Configs
import com.sumian.sddoctor.login.login.bean.LoginResponse
import com.sumian.sddoctor.main.MainActivity
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.util.PhoneNumberUtil
import kotlinx.android.synthetic.main.activity_bind_wechat.*

class BindWechatActivity : BaseActivity() {

    private var mSocialInfo = ""

    companion object {
        const val KEY_SOCIAL_INFO = "KEY_SOCIAL_INFO"

        private const val RESULT_CODE_IMAGE_CAPTCHA = 1

        fun launch(socialInfo: String) {
            val bundle = Bundle()
            bundle.putString(KEY_SOCIAL_INFO, socialInfo)
            ActivityUtils.startActivity(bundle, BindWechatActivity::class.java)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_bind_wechat
    }

    override fun initBundle(bundle: Bundle) {
        mSocialInfo = bundle.getString(KEY_SOCIAL_INFO) ?: ""
    }

    override fun initWidget() {
        super.initWidget()
        title_bar.setOnBackClickListener { run { finish() } }
        tv_send_captcha.setOnClickListener { onSendCaptchaClick() }
        tv_login.setOnClickListener { onLoginClick() }
        iv_phone_clear.setOnClickListener {
            iv_phone_clear.visibility = View.GONE
            et_phone.isActivated = false
            et_phone.setText("")
        }
        iv_captcha_clear.setOnClickListener {
            iv_captcha_clear.visibility = View.GONE
            et_captcha.isActivated = false
            et_captcha.setText("")
        }
    }

    private fun onSendCaptchaClick() {
        val phoneNumber = getPhoneNumberWithCheck() ?: return
        showLoading()
        val call = AppManager.getHttpService().requestLoginCaptcha(phoneNumber)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<Any>() {
            override fun onSuccess(response: Any?) {
                tv_send_captcha.startCountDown()
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                if (errorResponse.code == 4001) {
                    showImageCaptcha()
                } else {
                    ToastUtils.showShort(errorResponse.message)
                }
            }

            override fun onFinish() {
                dismissLoading()
            }
        })
    }

    private fun showImageCaptcha() {
        ImageCaptchaDialogActivity.startForResult(this, getPhoneNumberWithCheck()
                ?: "", RESULT_CODE_IMAGE_CAPTCHA)
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
                    tv_send_captcha.startCountDown()
                }
            }
        }
    }

    private fun onLoginClick() {
        val mobile = getPhoneNumberWithCheck() ?: return
        val captcha = getCaptchaWithCheck() ?: return
        showLoading()
        val call = AppManager.getHttpService().bindSocialiteAndLogin(mobile, captcha, 0, mSocialInfo)
        addCall(call)
        call
                .enqueue(object : BaseSdResponseCallback<LoginResponse>() {
                    override fun onSuccess(response: LoginResponse?) {
                        AppManager.getAccountViewModel().updateTokenInfoAndDoctorInfo(response)
                        ActivityUtils.finishAllActivities()
                        ActivityUtils.startActivity(MainActivity::class.java)
                    }

                    override fun onFailure(errorResponse: ErrorResponse) {
                        ToastUtils.showShort(errorResponse.message)
                    }

                    override fun onFinish() {
                        dismissLoading()
                    }
                })
    }

    /**
     * return valid phone number or null
     */
    private fun getPhoneNumberWithCheck(): String? {
        val phone = et_phone.text.toString()
        val mobileValidation = PhoneNumberUtil.checkMobileValidation(phone)
        return if (!mobileValidation) {
            ToastUtils.showShort(getString(R.string.phone_number_invalid))
            iv_phone_clear.visibility = View.VISIBLE
            et_phone.isActivated = true
            null
        } else {
            iv_phone_clear.visibility = View.GONE
            et_phone.isActivated = false
            phone
        }
    }

    private fun getCaptchaWithCheck(): String? {
        val captcha = et_captcha.text.toString()
        return if (captcha.length != Configs.CAPTCHA_LENGTH) {
            ToastUtils.showShort(getString(R.string.captcha_invalidate))
            iv_captcha_clear.visibility = View.VISIBLE
            et_captcha.isActivated = true
            null
        } else {
            iv_captcha_clear.visibility = View.GONE
            et_captcha.isActivated = false
            captcha
        }
    }
}
