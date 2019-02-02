package com.sumian.sddoctor.me.mywallet

import android.annotation.SuppressLint
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.base.SddBaseActivity
import com.sumian.sddoctor.me.authentication.AuthenticationHelper
import com.sumian.sddoctor.me.mywallet.bean.WalletBalance
import com.sumian.sddoctor.me.mywallet.bean.WithdrawAbility
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.util.MoneyUtil
import com.sumian.sddoctor.widget.dialog.SumianDialog
import kotlinx.android.synthetic.main.activity_withdraw_amount.*

@SuppressLint("SetJavaScriptEnabled")
/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/25 10:07
 * desc   :
 * version: 1.0
 */
class WithdrawAmountActivity : SddBaseActivity() {
    private var mBalance = 0L

    override fun getLayoutId(): Int {
        return R.layout.activity_withdraw_amount
    }

    override fun showBackNav(): Boolean {
        return true
    }

    override fun initWidget() {
        super.initWidget()
        mTitleBar.showMoreIcon(R.drawable.ic_nav_question)
        mTitleBar.setOnMenuClickListener { WithdrawRulesActivity.launch() }
        setTitle(R.string.available_withdraw_amount)
        bt_withdraw.setOnClickListener { queryWithdrawAbility() }
        tv_withdraw_record.setOnClickListener { ActivityUtils.startActivity(WithdrawRecordListActivity::class.java) }
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
                mBalance = response?.balance ?: 0L
                tv_wallet_balance.text = "￥" + MoneyUtil.fenToYuanString(mBalance)
            }
        })
    }

    private fun queryWithdrawAbility() {
        if (!AuthenticationHelper.checkAuthenticationStatusWithToast(this, R.string.after_authentication_you_can_withdraw)) return
        val call = AppManager.getHttpService().getWithdrawAbility()
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<WithdrawAbility>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }

            override fun onSuccess(response: WithdrawAbility?) {
                if (response == null) {
                    ToastUtils.showShort(R.string.error_unknown)
                    return
                }
                if (response.allowable) {
                    WithdrawActivity.launch(mBalance)
                } else {
                    SumianDialog(this@WithdrawAmountActivity)
                            .setTopIcon(R.drawable.ic_msg_icon_abnormal)
                            .setMessageText(response.message)
                            .setLeftBtn(R.string.confirm, null)
                            .show()
                }
            }
        })
    }
}