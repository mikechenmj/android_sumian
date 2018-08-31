package com.sumian.sd.account.login

import com.blankj.utilcode.util.ToastUtils
import com.sumian.sd.utils.AppUtil
import com.sumian.sd.account.bean.Token
import com.sumian.sd.account.bean.UserInfo
import com.sumian.sd.app.AppManager
import com.sumian.sd.network.callback.BaseResponseCallback

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/15 16:20
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class SetPasswordPresenter(var view: SetPasswordContract.View) : SetPasswordContract.Presenter {
    override fun setPassword(password: String) {
        val call = AppManager.getHttpService().modifyPassword(null, password, password)
        call.enqueue(object : BaseResponseCallback<UserInfo>() {

            override fun onSuccess(response: UserInfo?) {
                val accountViewModel = AppManager.getAccountViewModel()
                accountViewModel.updateUserInfo(response)
                LoginHelper.onLoginSuccess(accountViewModel.token)
            }

            override fun onFailure(code: Int, message: String) {
                ToastUtils.showShort(message)
            }

            override fun onFinish() {
                super.onFinish()
                view.dismissLoading()
            }
        })
    }

    override fun setPassword(token: Token, password: String) {
        val authorization = "Bearer " + token.token
        val call = AppManager.getHttpService().modifyPasswordWithToken(authorization, password, password)
        call.enqueue(object : BaseResponseCallback<UserInfo>() {

            override fun onSuccess(response: UserInfo?) {
                token.user = response
                AppManager.getAccountViewModel().updateToken(token)
                AppUtil.launchMain()
            }

            override fun onFailure(code: Int, message: String) {
                ToastUtils.showShort(message)
            }

            override fun onFinish() {
                super.onFinish()
                view.dismissLoading()
            }
        })
    }
}