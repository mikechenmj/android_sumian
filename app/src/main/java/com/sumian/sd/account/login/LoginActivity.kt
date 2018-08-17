package com.sumian.sd.account.login

import android.content.Intent
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BasePresenterActivity
import com.sumian.sd.BuildConfig
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.h5.HwSimpleWebActivity
import com.sumian.sd.main.MainActivity
import com.sumian.sd.setting.version.delegate.VersionDelegate
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BasePresenterActivity<LoginContract.Presenter>(), LoginContract.View {

    private lateinit var mVersionDelegate: VersionDelegate

    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }

    override fun initWidgetBefore() {
        super.initWidgetBefore()
        mPresenter = LoginPresenter(this)
    }

    override fun initWidget() {
        super.initWidget()
        iv_user_agreement.isSelected = true
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
        tv_forget_password.setOnClickListener { ValidatePhoneNumberActivity.launchForForgetPassword() }
        et_password.setStateChangeListener(object : SumianEditText.StateChangeListener {
            override fun onHighlightChange(highlight: Boolean) {
                ll_password_et_container.isActivated = highlight
            }
        })
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
            val captcha = et_captcha.getValidText()
            if (captcha == null) {
                InputCheckUtil.toastCaptchaInvalidate()
                return
            }
            mPresenter!!.loginByCaptcha(phone, captcha)
        } else {
            val password = et_password.getValidText()
            if (password == null) {
                InputCheckUtil.toastPasswordInvalidate()
                ll_password_et_container.isActivated = true
                return
            }
            mPresenter!!.loginByPassword(phone, password)
        }
    }


    /**
     * return valid phone number or null
     */
    private fun getPhoneNumberWithCheck(): String? {
        val phone = et_phone.getValidText()
        if (phone == null) {
            InputCheckUtil.toastPhoneNumberInvalidate()
        }
        return phone
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
