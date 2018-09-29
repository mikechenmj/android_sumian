package com.sumian.sd.account.login

import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.account.bean.Token
import com.sumian.sd.account.bean.UserInfo
import com.sumian.sd.app.AppManager
import com.sumian.sd.network.callback.BaseSdResponseCallback
import com.sumian.sd.utils.AppUtil

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
        val call = AppManager.getSdHttpService().modifyPassword(null, password, password)
        call.enqueue(object : BaseSdResponseCallback<UserInfo>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }

            override fun onSuccess(response: UserInfo?) {
                val accountViewModel = AppManager.getAccountViewModel()
                accountViewModel.updateUserInfo(response)
                LoginHelper.onLoginSuccess(accountViewModel.token)
            }

            override fun onFinish() {
                super.onFinish()
                view.dismissLoading()
            }
        })
    }

    override fun setPassword(token: Token, password: String) {
        val authorization = "Bearer " + token.token
        val call = AppManager.getSdHttpService().modifyPasswordWithToken(authorization, password, password)
        call.enqueue(object : BaseSdResponseCallback<UserInfo>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }

            override fun onSuccess(response: UserInfo?) {
                token.user = response
                AppManager.getAccountViewModel().updateToken(token)
                AppUtil.launchMain()
            }

            override fun onFinish() {
                super.onFinish()
                view.dismissLoading()
            }
        })
    }
}