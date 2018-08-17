package com.sumian.sd.account.login

import android.content.Intent
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BasePresenterActivity
import com.sumian.sd.BuildConfig
import com.sumian.sd.R
import com.sumian.sd.account.config.SumianConfig
import com.sumian.sd.app.AppManager
import com.sumian.sd.h5.HwSimpleWebActivity
import com.sumian.sd.main.MainActivity
import com.sumian.sd.setting.version.delegate.VersionDelegate
import com.sumian.sd.utils.PhoneNumberUtil
import kotlinx.android.synthetic.main.activity_login_v2.*

class LoginActivity : BasePresenterActivity<LoginContract.Presenter>(), LoginContract.View {

    private lateinit var mVersionDelegate: VersionDelegate

    override fun getLayoutId(): Int {
        return R.layout.activity_login_v2
    }

    override fun initWidgetBefore() {
        super.initWidgetBefore()
        mPresenter = LoginPresenter(this)
    }

    override fun initWidget() {
        super.initWidget()
        iv_user_agreement.isSelected = true
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
        iv_password_clear.setOnClickListener {
            iv_password_clear.visibility = View.GONE
            et_password.isActivated = false
            et_password.setText("")
        }
        tv_send_captcha.setOnClickListener {
            val number = getPhoneNumberWithCheck()
            if (number != null) {
                mPresenter!!.requestCaptcha(number)
            }
        }
        bt_login.setOnClickListener { onLoginClick() }
        iv_user_agreement.setOnClickListener { onIvUserAgreementClick() }
        tv_user_agreement.setOnClickListener { HwSimpleWebActivity.launchWithCompleteUrl(this, BuildConfig.HW_USER_AGREEMENT_URL) }
        tv_wechat_login.setOnClickListener { wechatLogin() }
        tv_captcha_login.setOnClickListener { turnOnCaptchaLogin(true) }
        tv_password_login.setOnClickListener { turnOnCaptchaLogin(false) }
        tv_register.setOnClickListener { ValidatePhoneNumberActivity.launchForRegister() }
        tv_forget_password.setOnClickListener { ValidatePhoneNumberActivity.launchForForgetPassword() }
    }

    override fun initData() {
        super.initData()
        mVersionDelegate = VersionDelegate.init()
    }

    override fun onResume() {
        super.onResume()
        mVersionDelegate.checkVersion(this)
    }

    private fun onLoginClick() {
        val phone = getPhoneNumberWithCheck() ?: return
        if (ll_captcha.visibility == View.VISIBLE) {
            val captcha = getCaptchaWithCheck() ?: return
            mPresenter!!.loginByCaptcha(phone, captcha)
        } else {
            val password = getPasswordWithCheck() ?: return
            mPresenter!!.loginByPassword(phone, password)
        }
    }

    private fun getCaptchaWithCheck(): String? {
        val captcha = et_captcha.text.toString()
        return if (captcha.length != SumianConfig.CAPTCHA_LENGTH) {
            ToastUtils.showShort(getString(R.string.captcha_invalidate))
            et_captcha.isActivated = true
            iv_captcha_clear.visibility = View.VISIBLE
            null
        } else {
            et_captcha.isActivated = false
            iv_captcha_clear.visibility = View.GONE
            captcha
        }
    }

    private fun getPasswordWithCheck(): String? {
        val password = et_password.text.toString()
        return if (!checkPasswordValidation(password)) {
            ToastUtils.showShort(getString(R.string.password_length_invalidate, SumianConfig.PASSWORD_LENGTH_MIN, SumianConfig.PASSWORD_LENGTH_MAX))
            et_password.isActivated = true
            iv_password_clear.visibility = View.VISIBLE
            null
        } else {
            et_password.isActivated = false
            iv_password_clear.visibility = View.GONE
            password
        }
    }

    /**
     * return valid phone number or null
     */
    private fun getPhoneNumberWithCheck(): String? {
        val phone = et_phone.text.toString()
        val mobileValidation = PhoneNumberUtil.checkMobileValidation(phone)
        return if (!mobileValidation) {
            ToastUtils.showShort(getString(R.string.phone_number_invalid))
            et_phone.isActivated = true
            iv_phone_clear.visibility = View.VISIBLE
            null
        } else {
            et_phone.isActivated = false
            iv_phone_clear.visibility = View.GONE
            phone
        }
    }

    private fun onIvUserAgreementClick() {
        iv_user_agreement.isSelected = !iv_user_agreement.isSelected
        bt_login.isEnabled = iv_user_agreement.isSelected
    }

    private fun wechatLogin() {
        mPresenter!!.loginByWechat(this)
    }

    private fun turnOnCaptchaLogin(turnOn: Boolean) {
        ll_captcha.visibility = if (turnOn) View.VISIBLE else View.GONE
        ll_password_et_container.visibility = if (turnOn) View.GONE else View.VISIBLE
        tv_password_login.visibility = if (turnOn) View.VISIBLE else View.GONE
        tv_captcha_login.visibility = if (turnOn) View.GONE else View.VISIBLE
    }

    private fun checkPasswordValidation(password: String): Boolean {
        return password.length >= SumianConfig.PASSWORD_LENGTH_MIN && password.length <= SumianConfig.PASSWORD_LENGTH_MAX
    }

    override fun launchMain() {
        ActivityUtils.startActivity(MainActivity::class.java)
        finish()
    }

    override fun onRequestCaptchaSuccess() {
        tv_send_captcha.startCountDown()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        AppManager.getOpenLogin().delegateActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        ActivityUtils.finishAllActivities()
    }
}
