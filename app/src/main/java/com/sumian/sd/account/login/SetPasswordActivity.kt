package com.sumian.sd.account.login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BasePresenterActivity
import com.sumian.hw.utils.AppUtil
import com.sumian.hw.utils.JsonUtil
import com.sumian.sd.R
import com.sumian.sd.account.bean.Token
import kotlinx.android.synthetic.main.activity_set_password.*

class SetPasswordActivity : BasePresenterActivity<SetPasswordContract.Presenter>(), SetPasswordContract.View {
    private var mLaunchType: String? = null
    private var mLaunchData: String? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_set_password
    }

    companion object {
        private const val KEY_LAUNCH_TYPE = "KEY_LAUNCH_TYPE"
        private const val KEY_LAUNCH_DATA = "KEY_LAUNCH_DATA"
        private const val LAUNCH_TYPE_RESET_PASSWORD = "LAUNCH_TYPE_RESET_PASSWORD"
        private const val LAUNCH_TYPE_OTHERS = "LAUNCH_TYPE_OTHERS"

        fun launch() {
            launch(LAUNCH_TYPE_OTHERS, null)
        }

        fun launchForResetPassword(token: String) {
            launch(LAUNCH_TYPE_RESET_PASSWORD, token)
        }

        private fun launch(launchType: String, launchData: String?) {
            val intent = Intent(ActivityUtils.getTopActivity(), SetPasswordActivity::class.java)
            intent.putExtra(KEY_LAUNCH_TYPE, launchType)
            intent.putExtra(KEY_LAUNCH_DATA, launchData)
            ActivityUtils.startActivity(intent)
        }
    }

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        mLaunchType = bundle.getString(KEY_LAUNCH_TYPE)
        mLaunchData = bundle.getString(KEY_LAUNCH_DATA)
    }

    override fun initWidgetBefore() {
        super.initWidgetBefore()
        mPresenter = SetPasswordPresenter(this)
    }

    override fun initWidget() {
        super.initWidget()
        title_bar.setOnBackClickListener { onBackPressed() }
        btn_confirm.setOnClickListener {
            val password = et_password.getValidText()
            val passwordConfirm = et_password_confirm.getValidText()
            if (!InputCheckUtil.isPasswordValid(password)) {
                InputCheckUtil.toastPasswordInvalidate()
                return@setOnClickListener
            }
            if (!InputCheckUtil.isPasswordValid(passwordConfirm)) {
                InputCheckUtil.toastPasswordInvalidate()
                return@setOnClickListener
            }
            if (!TextUtils.equals(password, passwordConfirm)) {
                ToastUtils.showShort(R.string.password_not_the_same)
                et_password.highlight(true)
                et_password_confirm.highlight(true)
                return@setOnClickListener
            }
            if (mLaunchType == LAUNCH_TYPE_RESET_PASSWORD) {
                val token = JsonUtil.fromJson<Token>(mLaunchData, Token::class.java)
                mPresenter!!.setPassword(token!!, password!!)
            } else {
                mPresenter!!.setPassword(password!!)
            }
        }

    }

    override fun onSetPasswordSuccess(data: String) {
    }

    override fun onSetPasswordFailed(msg: String) {
    }

    override fun onBackPressed() {
        if (mLaunchType == LAUNCH_TYPE_RESET_PASSWORD) {
            super.onBackPressed()
        } else {
            AppUtil.launchMainAndFinishAll()
        }
    }
}
