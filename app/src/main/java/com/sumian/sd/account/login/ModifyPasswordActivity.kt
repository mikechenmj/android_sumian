package com.sumian.sd.account.login

import android.text.TextUtils
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

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.setting_pwd)

        btn_confirm.setOnClickListener {
            val oldPassword = et_old_password.getValidText()
            val password = et_password.getValidText()
            val passwordConfirm = et_password_confirm.getValidText()
            if (!InputCheckUtil.isPasswordValid(oldPassword)) {
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

            val call = AppManager.getSdHttpService().modifyPassword(oldPassword, password!!, passwordConfirm!!)
            addCall(call)
            call.enqueue(object : BaseSdResponseCallback<UserInfo>() {
                override fun onSuccess(response: UserInfo?) {
                    AppManager.getAccountViewModel().updateUserInfo(response)
                    ToastUtils.showShort(R.string.modify_success)
                    finish()
                }

                override fun onFailure(errorResponse: ErrorResponse) {
                    ToastUtils.showShort(errorResponse.message)
                }
            })
        }
    }
}