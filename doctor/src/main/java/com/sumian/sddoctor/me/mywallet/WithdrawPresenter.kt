package com.sumian.sddoctor.me.mywallet

import com.sumian.common.base.BaseViewModel
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.me.mywallet.bean.WithdrawRecord
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/25 16:53
 * desc   :
 * version: 1.0
 */
class WithdrawPresenter(view: WithdrawContract.View) : BaseViewModel() {
    private val mView = view

    companion object {
        private const val SP_KEY_WITHDRAW_RULES = "key_withdraw_rules"
        private const val SP_FILE_NAME_WITHDRAW_RULES = "withdraw_rules"
    }

    fun withdraw(amount: Long) {
        val call = AppManager.getHttpService().withdraw(amount)
        mView.addCalls(call)
        call.enqueue(object : BaseSdResponseCallback<WithdrawRecord>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                mView.withdrawFail(errorResponse.code, errorResponse.message)
            }

            override fun onSuccess(response: WithdrawRecord?) {
                mView.withdrawSuccess(response!!)
            }
        })
    }
}