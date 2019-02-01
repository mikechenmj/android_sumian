package com.sumian.sddoctor.account.activity

import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.mvp.IPresenter
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.InputCheckUtil
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.base.SddBaseActivity
import com.sumian.sddoctor.base.SddBaseViewModelActivity
import com.sumian.sddoctor.constants.Configs
import com.sumian.sddoctor.login.login.LoginActivity
import com.sumian.sddoctor.login.login.bean.DoctorInfo
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import kotlinx.android.synthetic.main.activity_modify_password.*

/**
 * Created by jzz
 * on 2017/10/13.
 * desc:
 */

class ModifyPasswordActivity : SddBaseActivity() {
    companion object {

        fun start() {
            ActivityUtils.startActivity(ActivityUtils.getTopActivity(), ModifyPasswordActivity::class.java)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_modify_password
    }

    override fun showBackNav(): Boolean {
        return true
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.set_password)
        vg_old_password.visibility = if (hasOldPassword()) View.VISIBLE else View.GONE
        bt_complete.setOnClickListener { submitInput() }
    }

    private fun submitInput() {
        val oldPwd = et_old_pwd.text.toString().trim { it <= ' ' }
        val newPwd = et_new_pwd.text.toString().trim { it <= ' ' }
        val reNewPwd = et_check_new_pwd.text.toString().trim { it <= ' ' }
        if (hasOldPassword()) {
            if (!InputCheckUtil.checkInput(et_old_pwd, getString(R.string.old_pwd), Configs.PASSWORD_LENGTH_MIN, Configs.PASSWORD_LENGTH_MAX)) {
                return
            }
        }
        if (!InputCheckUtil.checkInput(et_new_pwd, getString(R.string.password), Configs.PASSWORD_LENGTH_MIN, Configs.PASSWORD_LENGTH_MAX)) {
            return
        }
        if (!InputCheckUtil.checkInput(et_check_new_pwd, getString(R.string.confirm_password), Configs.PASSWORD_LENGTH_MIN, Configs.PASSWORD_LENGTH_MAX)) {
            return
        }
        if (et_new_pwd.text.toString() != et_check_new_pwd.text.toString()) {
            ToastUtils.showShort(R.string.verify_pwd_error_hint)
        }
        doResetPwd(oldPwd, newPwd, reNewPwd)
    }

    private fun hasOldPassword() =
            AppManager.getAccountViewModel().getDoctorInfo().value?.set_password == true

    private fun doResetPwd(oldPwd: String?, newPwd: String, newPwdConfirmation: String) {
        showLoading()
        val call = AppManager.getHttpService().updatePassword(oldPwd, newPwd, newPwdConfirmation)
        call.enqueue(object : BaseSdResponseCallback<DoctorInfo>() {

            override fun onSuccess(response: DoctorInfo?) {
                AppManager.getAccountViewModel().updateDoctorInfo(response, true)
                ToastUtils.showShort(getString(R.string.setting_set_pwd_success_hint))
                ActivityUtils.finishAllActivities()
                LoginActivity.start()
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
}
