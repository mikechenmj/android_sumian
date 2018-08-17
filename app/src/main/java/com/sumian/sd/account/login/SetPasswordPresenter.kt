package com.sumian.sd.account.login

import com.blankj.utilcode.util.ToastUtils
import com.sumian.sd.account.bean.Token
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
        val call = AppManager.getHttpService().modifyPassword(password, password)
        call.enqueue(object : BaseResponseCallback<Token>() {

            override fun onSuccess(response: Token?) {
                LoginHelper.onLoginSuccess(response)
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