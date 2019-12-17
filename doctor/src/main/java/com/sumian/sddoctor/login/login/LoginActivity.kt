package com.sumian.sddoctor.login.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseActivity
import com.sumian.common.statistic.StatUtil
import com.sumian.sddoctor.BuildConfig
import com.sumian.sddoctor.R
import com.sumian.sddoctor.account.delegate.VersionDelegate
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.constants.Configs
import com.sumian.sddoctor.constants.StatConstants
import com.sumian.sddoctor.main.MainActivity
import com.sumian.sddoctor.util.PhoneNumberUtil
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity(), LoginContract.View {

    private var mIsLoginWithCaptcha: Boolean = true // true login with captcha, false login with password
    private lateinit var mPresenter: LoginPresenter

    companion object {
        private const val RESULT_CODE_IMAGE_CAPTCHA = 1
        fun start() {
            ActivityUtils.startActivity(ActivityUtils.getTopActivity(), LoginActivity::class.java)
        }
    }

    private val mVersionDelegate: VersionDelegate by lazy {
        VersionDelegate.init()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }

    override fun initWidgetBefore() {
        mPresenter = LoginPresenter(this)
    }

    override fun initWidget() {
        super.initWidget()
        StatUtil.event(StatConstants.enter_login_page)
        iv_user_agreement.isSelected = true
        iv_phone_clear.setOnClickListener {
            iv_phone_clear.visibility = GONE
            et_phone.isActivated = false
            et_phone.setText("")
        }
        iv_captcha_clear.setOnClickListener {
            iv_captcha_clear.visibility = GONE
            et_captcha.isActivated = false
            et_captcha.setText("")
        }
        iv_password_clear.setOnClickListener {
            iv_password_clear.visibility = GONE
            et_password.isActivated = false
            et_password.setText("")
        }
        tv_send_captcha.setOnClickListener {
            StatUtil.event(StatConstants.click_login_page_captcha)
            val number = getPhoneNumberWithCheck()
            if (number != null) {
                mPresenter.requestCaptcha(number)
            }
        }
        bt_login.setOnClickListener { onLoginClick() }
        tv_user_agreement.setOnClickListener { ActivityUtils.startActivity(UserProtocolActivity::class.java) }
        tv_wechat_login.setOnClickListener { wechatLogin() }
        tv_captcha_login.setOnClickListener { turnOnCaptchaLogin(true) }
        tv_password_login.setOnClickListener { turnOnCaptchaLogin(false) }
//        tv_register.setOnClickListener { ActivityUtils.startActivity(RegisterActivity::class.java) }
        tv_visitor.setOnClickListener {
            mPresenter.loginByPassword(BuildConfig.VISITOR_MOBILE, BuildConfig.VISITOR_PWD)
        }
    }

    override fun onStart() {
        super.onStart()
        mVersionDelegate.checkVersion(this)
    }

    private fun onLoginClick() {
        val phone = getPhoneNumberWithCheck() ?: return
        if (mIsLoginWithCaptcha) {
            val captcha = getCaptchaWithCheck() ?: return
            mPresenter.loginByCaptcha(phone, captcha)
        } else {
            val password = getPasswordWithCheck() ?: return
            mPresenter.loginByPassword(phone, password)
        }
    }

    private fun getCaptchaWithCheck(): String? {
        val captcha = et_captcha.text.toString()
        return if (captcha.length != Configs.CAPTCHA_LENGTH) {
            ToastUtils.showShort(getString(R.string.captcha_invalidate))
            et_captcha.isActivated = true
            iv_captcha_clear.visibility = VISIBLE
            null
        } else {
            et_captcha.isActivated = false
            iv_captcha_clear.visibility = GONE
            captcha
        }
    }

    private fun getPasswordWithCheck(): String? {
        val password = et_password.text.toString()
        return if (!checkPasswordValidation(password)) {
            ToastUtils.showShort(getString(R.string.password_length_invalidate, Configs.PASSWORD_LENGTH_MIN, Configs.PASSWORD_LENGTH_MAX))
            et_password.isActivated = true
            iv_password_clear.visibility = VISIBLE
            null
        } else {
            et_password.isActivated = false
            iv_password_clear.visibility = GONE
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
            iv_phone_clear.visibility = VISIBLE
            null
        } else {
            et_phone.isActivated = false
            iv_phone_clear.visibility = GONE
            phone
        }
    }

    private fun onIvUserAgreementClick() {
        iv_user_agreement.isSelected = !iv_user_agreement.isSelected
        bt_login.isEnabled = iv_user_agreement.isSelected
    }

    private fun wechatLogin() {
        mPresenter.loginByWechat(this)
    }

    private fun turnOnCaptchaLogin(turnOn: Boolean) {
        mIsLoginWithCaptcha = turnOn
        ll_captcha.visibility = if (turnOn) VISIBLE else GONE
        fl_password_et_container.visibility = if (turnOn) GONE else VISIBLE
        tv_password_login.visibility = if (turnOn) VISIBLE else GONE
        tv_captcha_login.visibility = if (turnOn) GONE else VISIBLE
    }

    private fun checkPasswordValidation(password: String): Boolean {
        return password.length >= Configs.PASSWORD_LENGTH_MIN && password.length <= Configs.PASSWORD_LENGTH_MAX
    }

    override fun launchMain() {
        ActivityUtils.startActivity(MainActivity::class.java)
        finish()
    }

    override fun onRequestCaptchaSuccess() {
        tv_send_captcha.startCountDown()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        ActivityUtils.finishAllActivities()
    }

    override fun getContext(): Context {
        return this
    }

    override fun showLoading() {
        super<BaseActivity>.showLoading()
    }

    override fun dismissLoading() {
        super<BaseActivity>.dismissLoading()
    }

    override fun onRequestCaptchaFail(code: Int) {
        if (code == 4001) {
            showImageCaptcha()
        }
    }

    private fun showImageCaptcha() {
        ImageCaptchaDialogActivity.startForResult(this, RESULT_CODE_IMAGE_CAPTCHA)
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
                    mPresenter.requestCaptcha(getPhoneNumberWithCheck() ?: "", id, phrase)
                }
            }
        }
    }
}
