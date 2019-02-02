package com.sumian.sddoctor.me.mywallet

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.TimeUtilV2
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.base.SddBaseActivity
import com.sumian.sddoctor.me.mywallet.bean.SettlingRecord
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.util.MoneyUtil
import kotlinx.android.synthetic.main.activity_settling_record_detail.*
import kotlinx.android.synthetic.main.view_wallet_detail_item.view.*

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/25 19:58
 * desc   :
 * version: 1.0
 */
@Suppress("DEPRECATION")
class SettlingRecordDetailActivity : SddBaseActivity() {
    override fun showBackNav(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_settling_record_detail
    }

    companion object {
        private const val KEY_RECORD_ID = "key_record_id"

        fun launch(id: Int) {
            ActivityUtils.startActivity(getLaunchIntent(id))
        }

        fun getLaunchIntent(id: Int): Intent {
            val intent = Intent(ActivityUtils.getTopActivity(), SettlingRecordDetailActivity::class.java)
            intent.putExtra(KEY_RECORD_ID, id)
            return intent
        }
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.detail_detail)
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(record: SettlingRecord) {
        tv_amount.text = MoneyUtil.fenToYuanString(record.amount)
        item_type.tv_end.text = record.getTypeString(this)
        item_time.tv_end.text = TimeUtilV2.formatYYYYMMDDHHMM(record.createdAt)
        item_serial_number.tv_end.text = record.sn
        item_content.tv_end.text = record.content
        item_progress.tv_end.text = record.getStatusText(this)
        item_progress.tv_end.setTextColor(resources.getColor(record.getStatusTextColorRes()))
        item_in_account_time.tv_end.text = TimeUtilV2.formatYYYYMMDDHHMM(record.creditedAt)
        item_in_account_time.visibility = if (record.status == 0) View.VISIBLE else View.GONE
        tv_explain.text = record.explanation
        vg_explain.visibility = if (record.status == 1) View.VISIBLE else View.GONE
        tv_contact_us.visibility = if (record.status == 0) View.VISIBLE else View.GONE
    }

    override fun initData() {
        super.initData()
        val call = AppManager.getHttpService().getPendingIncomeDetail(intent.getIntExtra(KEY_RECORD_ID, 0))
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<SettlingRecord>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }

            override fun onSuccess(response: SettlingRecord?) {
                updateUI(response!!)
            }
        })
    }
}