package com.sumian.sddoctor.login.register

import android.app.Activity
import android.content.Intent
import android.view.View
import android.view.View.VISIBLE
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.base.BaseFragment
import com.sumian.sddoctor.login.login.BindWechatActivity
import com.sumian.sddoctor.login.login.ImageCaptchaDialog
import com.sumian.sddoctor.login.login.ImageCaptchaDialogActivity
import com.sumian.sddoctor.login.login.bean.LoginResponse
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.util.EditTextUtil
import kotlinx.android.synthetic.main.fragment_register_verify_phone_number.*

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/28 16:36
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class RegisterVerifyPhoneNumberFragment : BaseFragment() {

    companion object {
        private const val RESULT_CODE_IMAGE_CAPTCHA = 1
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_register_verify_phone_number
    }

    override fun initWidget(root: View?) {
        super.initWidget(root)
        tv_send_captcha.setOnClickListener { onSendCaptchaClick() }
        tv_next_step.setOnClickListener { onNextStepClick() }
        iv_phone_clear.setOnClickListener { EditTextUtil.clearEditText(et_phone, iv_phone_clear) }
        iv_captcha_clear.setOnClickListener { EditTextUtil.clearEditText(et_captcha, iv_captcha_clear) }
    }

    private fun onSendCaptchaClick() {
        val phone = EditTextUtil.getPhoneNumberWithCheck(context!!, et_phone)
        if (phone == null) {
            iv_phone_clear.visibility = VISIBLE
            et_phone.isActivated = true
            return
        }
        showLoading()
        val call = AppManager.getHttpService().requestLoginCaptcha(phone)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<Any>() {
            override fun onSuccess(response: Any?) {
                LogUtils.d(response)
                tv_send_captcha.startCountDown()
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                LogUtils.d(errorResponse)
                ToastUtils.showShort(errorResponse.message)
                if (errorResponse.code == 4001) {
                    showImageCaptcha()
                }
            }

            override fun onFinish() {
                super.onFinish()
                dismissLoading()
            }
        })
    }

    private fun showImageCaptcha() {
        ImageCaptchaDialogActivity.startForResult(activity!!, RESULT_CODE_IMAGE_CAPTCHA)
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
                    var id = data.getStringExtra(ImageCaptchaDialog.EXTRA_CAPTCHA_ID)
                    var phrase = data.getStringExtra(ImageCaptchaDialog.EXTRA_CAPTCHA_PHRASE)
                    requestCaptchaAfterImageCaptcha(id, phrase)
                }
            }
        }
    }

    private fun requestCaptchaAfterImageCaptcha(captchaId: String, captchaPhrase: String) {
        val number = EditTextUtil.getPhoneNumberWithCheck(context!!, et_phone)
        if (number != null) {
            val call = AppManager.getHttpService().requestLoginCaptcha(number, captchaId, captchaPhrase)
            addCall(call)
            call.enqueue(object : BaseSdResponseCallback<Any>() {
                override fun onSuccess(response: Any?) {
                    LogUtils.d(response)
                    tv_send_captcha.startCountDown()
                }

                override fun onFailure(errorResponse: ErrorResponse) {
                    LogUtils.d(errorResponse)
                    ToastUtils.showShort(errorResponse.message)
                    if (errorResponse.code == 4001) {
                        showImageCaptcha()
                    }
                }

                override fun onFinish() {
                    super.onFinish()
                    dismissLoading()
                }
            })
        }
    }

    private fun onNextStepClick() {
        val phone = EditTextUtil.getPhoneNumberWithCheck(context!!, et_phone)
        EditTextUtil.errorEditText(et_phone, iv_phone_clear, phone == null)
        val captcha = EditTextUtil.getCaptchaWithCheck(context!!, et_captcha)
        EditTextUtil.errorEditText(et_captcha, iv_captcha_clear, captcha == null)
        if (phone == null || captcha == null) {
            return
        }
        val inviteCode = et_invite_code.text.toString().trim()
        showLoading()
        val call = AppManager.getHttpService().signUp(phone, captcha, inviteCode)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<LoginResponse>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }

            override fun onSuccess(response: LoginResponse?) {
                AppManager.onLoginSuccess(response)
            }

            override fun onFinish() {
                dismissLoading()
            }
        })
    }
}