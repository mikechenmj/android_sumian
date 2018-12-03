package com.sumian.sd.account.login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BasePresenterActivity
import com.sumian.common.mvp.IPresenter
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.R
import com.sumian.sd.account.bean.Token
import com.sumian.sd.account.bean.UserInfo
import com.sumian.sd.app.AppManager
import com.sumian.sd.network.callback.BaseSdResponseCallback
import kotlinx.android.synthetic.main.activity_modify_password.*

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/14 17:09
 * desc   :
 * version: 1.0
 */
class ModifyPasswordActivity : BasePresenterActivity<IPresenter>() {
    private var mNeedOldPassword = false
    private var mToken: String? = null

    companion object {
        private const val KEY_NEED_OLD_PASSWORD = "NEED_OLD_PASSWORD"
        private const val KEY_TOKEN = "KEY_TOKEN"

        fun start(needOldPassword: Boolean = false, token: String?) {
            val intent = Intent(ActivityUtils.getTopActivity(), ModifyPasswordActivity::class.java)
            intent.putExtra(KEY_NEED_OLD_PASSWORD, needOldPassword)
            intent.putExtra(KEY_TOKEN, token)
            ActivityUtils.startActivity(intent)
        }
    }

    override fun showBackNav(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_modify_password
    }

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        mNeedOldPassword = bundle.getBoolean(KEY_NEED_OLD_PASSWORD)
        mToken = bundle.getString(KEY_TOKEN)
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.set_password)
        et_old_password.visibility = if (mNeedOldPassword) View.VISIBLE else View.GONE
        et_password.setHint(if (mNeedOldPassword) R.string.please_set_new_password else R.string.please_set_password)
        et_password_confirm.setHint(if (mNeedOldPassword) R.string.repeat_new_password else R.string.repeat_password)
        btn_confirm.setOnClickListener {
            val oldPassword = et_old_password.getValidText()
            val password = et_password.getValidText()
            val passwordConfirm = et_password_confirm.getValidText()
            if (mNeedOldPassword && !InputCheckUtil.isPasswordValid(oldPassword)) {
                InputCheckUtil.toastPasswordInvalidate()
                return@setOnClickListener
            }
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

            if (TextUtils.isEmpty(mToken)) {
                if (mNeedOldPassword) {
                    changePassword(oldPassword, password, passwordConfirm)
                } else {
                    changePassword(null, password, passwordConfirm)
                }
            } else {
                setPassword(mToken!!, password!!)
            }
        }
    }

    private fun changePassword(oldPassword: String?, password: String?, passwordConfirm: String?) {
        showLoading()
        val call = AppManager.getSdHttpService().modifyPassword(oldPassword, password!!, passwordConfirm!!)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<UserInfo>() {
            override fun onSuccess(response: UserInfo?) {
                AppManager.getAccountViewModel().updateUserInfo(response)
                ToastUtils.showShort(R.string.set_success)
                ActivityUtils.finishAllActivities()
                ActivityUtils.startActivity(this@ModifyPasswordActivity, LoginActivity::class.java)
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }

            override fun onFinish() {
                super.onFinish()
                dismissLoading()
            }
        })
    }

    private fun setPassword(token: String, password: String) {
        showLoading()
        val authorization = "Bearer " + token
        val call = AppManager.getSdHttpService().modifyPasswordWithToken(authorization, password, password)
        call.enqueue(object : BaseSdResponseCallback<UserInfo>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }

            override fun onSuccess(response: UserInfo?) {
                ActivityUtils.finishAllActivities()
                ActivityUtils.startActivity(LoginActivity::class.java)
            }

            override fun onFinish() {
                super.onFinish()
                dismissLoading()
            }
        })
    }
}