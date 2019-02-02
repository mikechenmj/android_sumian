package com.sumian.sddoctor.me.mywallet

import com.sumian.common.base.BaseShowLoadingView
import com.sumian.sddoctor.me.mywallet.bean.WithdrawRecord
import retrofit2.Call

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/25 16:49
 * desc   :
 * version: 1.0
 */
interface WithdrawContract {
    interface View : BaseShowLoadingView {
        fun addCalls(call: Call<*>)
        fun withdrawSuccess(withdrawRecord: WithdrawRecord)
        fun withdrawFail(code: Int, message: String)
    }

}