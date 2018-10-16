package com.sumian.sd.account.login

import android.text.TextUtils
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseBackActivity
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.R
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
class ModifyPasswordActivity : BaseBackActivity() {
    override fun getChildContentId(): Int {
        return R.layout.activity_modify_password
    }

    private val mHasPassword = AppManager.getAccountViewModel().userInfo.hasPassword

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.set_password)
        et_old_password.visibility = if (mHasPassword) View.VISIBLE else View.GONE
        et_password.setHint(if (mHasPassword) R.string.please_set_new_password else R.string.please_set_password)
        et_password_confirm.setHint(if (mHasPassword) R.string.repeat_new_password else R.string.repeat_password)
        setTitle(R.string.setting_pwd)

        btn_confirm.setOnClickListener {
            val oldPassword = et_old_password.getValidText()
            val password = et_password.getValidText()
            val passwordConfirm = et_password_confirm.getValidText()
            if (mHasPassword && !InputCheckUtil.isPasswordValid(oldPassword)) {
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

            if (mHasPassword) {
                changePassword(oldPassword, password, passwordConfirm)
            } else {
                changePassword(null, password, passwordConfirm)
            }
        }
    }


    private fun changePassword(oldPassword: String?, password: String?, passwordConfirm: String?) {
        val call = AppManager.getSdHttpService().modifyPassword(oldPassword, password!!, passwordConfirm!!)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<UserInfo>() {
            override fun onSuccess(response: UserInfo?) {
                AppManager.getAccountViewModel().updateUserInfo(response)
                ToastUtils.showShort(R.string.set_success)
                finish()
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }
        })
    }
}