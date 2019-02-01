package com.sumian.sddoctor.me.mywallet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.JsonUtil
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.me.mywallet.bean.WithdrawRule
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/11/15 09:54
 * desc   :
 * version: 1.0
 */
class WithdrawRuleViewModel : ViewModel() {
    private val mRuleLiveData = MutableLiveData<WithdrawRule>()

    companion object {
        private const val SP_FILE_NAME_WITHDRAW_RULES = "withdraw_rules"
        private const val SP_KEY_WITHDRAW_RULES = "key_withdraw_rules"
    }

    init {
        queryRule()
    }

    fun queryRule() {
        getLocalRules()?.let {
            mRuleLiveData.value = it
        }
        val call = AppManager.getHttpService().getWithdrawRule()
        call.enqueue(object : BaseSdResponseCallback<WithdrawRule>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }

            override fun onSuccess(response: WithdrawRule?) {
                if (response == null) {
                    return
                }
                saveRules(response)
                mRuleLiveData.value = response
            }
        })
    }

    fun getRuleLiveData(): LiveData<WithdrawRule> {
        return mRuleLiveData
    }

    private fun getLocalRules(): WithdrawRule? {
        val json = SPUtils.getInstance(SP_FILE_NAME_WITHDRAW_RULES).getString(SP_KEY_WITHDRAW_RULES, "")
        return JsonUtil.fromJson(json, WithdrawRule::class.java)
    }

    private fun saveRules(rules: WithdrawRule?) {
        return SPUtils.getInstance(SP_FILE_NAME_WITHDRAW_RULES).put(SP_KEY_WITHDRAW_RULES, JsonUtil.toJson(rules))
    }

}