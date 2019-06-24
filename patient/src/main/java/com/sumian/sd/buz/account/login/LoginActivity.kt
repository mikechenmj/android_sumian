package com.sumian.sd.buz.account.login

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.qmuiteam.qmui.util.QMUISpanHelper
import com.sumian.common.base.BaseViewModelActivity
import com.sumian.common.statistic.StatUtil
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.stat.StatConstants
import com.sumian.sd.buz.version.VersionManager
import com.sumian.sd.common.h5.H5Uri
import com.sumian.sd.common.h5.SimpleWebActivity
import com.sumian.sd.common.utils.StatusBarUtil
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseViewModelActivity<LoginPresenter>(), LoginContract.View {

    companion object {
        @JvmStatic
        fun show() {
            ActivityUtils.startActivity(LoginActivity::class.java)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }

    override fun initWidgetBefore() {
        super.initWidgetBefore()
        mViewModel = LoginPresenter(this)
    }

    override fun initWidget() {
        super.initWidget()
        StatUtil.event(StatConstants.page_login)
        StatusBarUtil.setStatusBarTextColorDark(this, true)
        iv_user_agreement.isSelected = true
        tv_send_captcha.setOnClickListener {
            val number = getPhoneNumberWithCheck()
            if (number != null) {
                onRequestCaptchaSuccess()
                mViewModel!!.requestCaptcha(number)
                StatUtil.event(StatConstants.click_captcha, mapOf("usage" to "登录注册", "mobile" to number))
            }
        }
        bt_login.setOnClickListener { onLoginClick() }
        iv_user_agreement.setOnClickListener { onIvUserAgreementClick() }
        tv_user_agreement.setOnClickListener { SimpleWebActivity.launch(this, H5Uri.USER_AGREEMENT_URL) }
        tv_user_privacy_policy.setOnClickListener { SimpleWebActivity.launch(this, H5Uri.USER_POLICY_URL) }

        tv_wechat_login?.apply {

            //val drawable = getDrawable(R.drawable.ic_sign_wechat) as Drawable

            val oldBmp = BitmapFactory.decodeResource(resources, R.drawable.ic_sign_wechat)
            val newBmp = Bitmap.createScaledBitmap(oldBmp, resources.getDimensionPixelOffset(R.dimen.space_18), resources.getDimensionPixelOffset(R.dimen.space_14), true)
            oldBmp.recycle()
            val bitmapDrawable = BitmapDrawable(resources, newBmp)
            //drawable.setBounds(0, 0, resources.getDimensionPixelOffset(R.dimen.space_22), resources.getDimensionPixelOffset(R.dimen.space_18))
            text = QMUISpanHelper.generateSideIconText(true, resources.getDimensionPixelOffset(R.dimen.space_8), getString(R.string.wechat_login), bitmapDrawable)
            //newBmp.recycle()
            setOnClickListener { wechatLogin() }

        }
        tv_captcha_login.setOnClickListener { turnOnCaptchaLogin(true) }
        tv_password_login.setOnClickListener { turnOnCaptchaLogin(false) }
        tv_forget_password.setOnClickListener { ValidatePhoneNumberActivity.launchForForgetPassword() }
        et_password.setStateChangeListener(object : SumianEditText.StateChangeListener {
            override fun onHighlightChange(highlight: Boolean) {
                ll_password_et_container.isActivated = highlight
            }
        })
    }

    override fun onStart() {
        super.onStart()
        AppManager.getAccountViewModel().clearToken()
        VersionManager.queryAppVersion(true)
    }

    private fun onLoginClick() {
        val phone = getPhoneNumberWithCheck() ?: return
        if (ll_captcha.visibility == View.VISIBLE) {
            val captcha = et_captcha.getValidText()
            if (captcha == null) {
                InputCheckUtil.toastCaptchaInvalidate()
                return
            }
            mViewModel!!.loginByCaptcha(phone, captcha)
        } else {
            val password = et_password.getValidText()
            if (password == null) {
                InputCheckUtil.toastPasswordInvalidate()
                ll_password_et_container.isActivated = true
                return
            }
            mViewModel!!.loginByPassword("1234567891", "123456") // todo remove
//            mViewModel!!.loginByPassword(phone, password)
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
        mViewModel!!.loginByWechat(this)
        StatUtil.event(StatConstants.click_wechat_login)
    }

    private fun turnOnCaptchaLogin(turnOn: Boolean) {
        ll_captcha.visibility = if (turnOn) View.VISIBLE else View.GONE
        ll_password_et_container.visibility = if (turnOn) View.GONE else View.VISIBLE
        tv_password_login.visibility = if (turnOn) View.VISIBLE else View.GONE
        tv_captcha_login.visibility = if (turnOn) View.GONE else View.VISIBLE
    }

    override fun onRequestCaptchaSuccess() {
        tv_send_captcha.startCountDown()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        AppManager.getOpenLogin().delegateActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        ActivityUtils.finishAllActivities()
    }
}
