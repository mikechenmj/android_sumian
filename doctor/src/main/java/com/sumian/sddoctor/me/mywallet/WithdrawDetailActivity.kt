package com.sumian.sddoctor.me.mywallet

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.ColorCompatUtil
import com.sumian.common.utils.MoneyUtil
import com.sumian.common.utils.TimeUtilV2
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.base.SddBaseActivity
import com.sumian.sddoctor.me.mywallet.bean.WithdrawRecord
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import kotlinx.android.synthetic.main.activity_withdraw_record_detail.*

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/25 19:58
 * desc   :
 * version: 1.0
 */
class WithdrawDetailActivity : SddBaseActivity() {
    override fun showBackNav(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_withdraw_record_detail
    }

    companion object {
        private const val KEY_WITHDRAW_ID = "key_withdraw_id"

        fun launch(id: Int) {
            ActivityUtils.startActivity(getLaunchIntent(id))
        }

        fun getLaunchIntent(id: Int): Intent {
            val intent = Intent(ActivityUtils.getTopActivity(), WithdrawDetailActivity::class.java)
            intent.putExtra(KEY_WITHDRAW_ID, id)
            return intent
        }
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.record_detail)
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(record: WithdrawRecord) {
        tv_amount.text = "-" + MoneyUtil.fenToYuanString(record.amount)
        tv_apply_time.text = TimeUtilV2.formatYYYYMMDDHHMM(record.getCreateAtInMillis())
        tv_approve_time.text = TimeUtilV2.formatYYYYMMDDHHMM(record.getUpdateAtInMillis())
        tv_progress.text = getText(record.getStatusTextRes())
        tv_progress.setTextColor(ColorCompatUtil.getColor(this, record.getStatusTextColorRes()))
        vg_approve_time.visibility = if (record.status == 0) View.GONE else View.VISIBLE
        vg_remark.visibility = if (record.status == 2) View.VISIBLE else View.GONE
        tv_remark.text = record.remark
        tv_bottom_hint.text = getText(record.getStatusHintTextRes())
    }

    override fun initData() {
        super.initData()

        val call = AppManager.getHttpService().getWithdrawDetail(intent.getIntExtra(KEY_WITHDRAW_ID, 0))
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<WithdrawRecord>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }

            override fun onSuccess(response: WithdrawRecord?) {
                updateUI(response!!)
            }
        })
    }
}