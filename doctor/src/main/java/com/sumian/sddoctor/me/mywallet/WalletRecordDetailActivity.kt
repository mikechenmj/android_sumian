package com.sumian.sddoctor.me.mywallet

import android.annotation.SuppressLint
import android.content.Intent
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.mvp.IPresenter
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.TimeUtilV2
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.base.SddBaseActivity
import com.sumian.sddoctor.base.SddBaseViewModelActivity
import com.sumian.sddoctor.me.mywallet.bean.WalletDetail
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.util.MoneyUtil
import kotlinx.android.synthetic.main.activity_wallet_record_detail.*

@SuppressLint("Registered")
/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/18 16:48
 * desc   :
 * version: 1.0
 */
class WalletRecordDetailActivity : SddBaseActivity() {

    override fun showBackNav(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_wallet_record_detail
    }

    companion object {
        private const val KEY_DETAIL_ID = "DETAIL_ID"

        fun launch(id: Int) {
            ActivityUtils.startActivity(getLaunchIntent(id))
        }

        fun getLaunchIntent(id: Int): Intent {
            val intent = Intent(ActivityUtils.getTopActivity(), WalletRecordDetailActivity::class.java)
            intent.putExtra(KEY_DETAIL_ID, id)
            return intent
        }
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.wallet_detail)
    }

    override fun initData() {
        super.initData()
        val call = AppManager.getHttpService().getWalletDetail(intent.getIntExtra(KEY_DETAIL_ID, 0))
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<WalletDetail>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }

            override fun onSuccess(response: WalletDetail?) {
                updateUI(response!!)
            }
        })
    }

    private fun updateUI(walletDetail: WalletDetail) {
        tv_amount.text = MoneyUtil.fenToYuanStringWithSign(walletDetail.getSignedAmount())
        sdv_type.setContentText(walletDetail.getTypeString())
        sdv_trade_time.setContentText(TimeUtilV2.formatYYYYMMDDHHMM(walletDetail.getCreateInMillis()))
        sdv_sn.setContentText(walletDetail.sn)
        sdv_content.setContentText(walletDetail.content)
    }
}