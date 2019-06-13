package com.sumian.sddoctor.me.mywallet

import android.annotation.SuppressLint
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.MoneyUtil
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.base.SddBaseActivity
import com.sumian.sddoctor.me.mywallet.bean.WalletBalance
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import kotlinx.android.synthetic.main.activity_settling_amount.*

@SuppressLint("SetJavaScriptEnabled")
/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/25 10:07
 * desc   :
 * version: 1.0
 */
class PendingIncomeAmountActivity : SddBaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_settling_amount
    }

    override fun showBackNav(): Boolean {
        return true
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.settling_amount)
        tv_see_detail.setOnClickListener { ActivityUtils.startActivity(PendingIncomeListActivity::class.java) }
    }

    override fun initData() {
        super.initData()
        queryBalance()
    }

    private fun queryBalance() {
        val call = AppManager.getHttpService().getWalletBalance()
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<WalletBalance>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }

            @SuppressLint("SetTextI18n")
            override fun onSuccess(response: WalletBalance?) {
                tv_settling_amount.text = MoneyUtil.fenToYuanString(response?.pending_income
                        ?: 0L)
            }
        })
    }
}