package com.sumian.sd.buz.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.LogUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.base.SdBaseActivity
import com.sumian.sd.base.SdBasePresenter
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import com.sumian.sd.common.pay.bean.OrderDetail
import com.sumian.sd.common.utils.TimeUtil
import kotlinx.android.synthetic.main.activity_refund.*

/**
 * 退款
 */
class RefundActivity : SdBaseActivity<SdBasePresenter<Any>>() {
    private var mOrderNo: String = ""

    override fun getLayoutId(): Int {
        return R.layout.activity_refund
    }

    companion object {
        private const val KEY_ORDER_NO = "KEY_ORDER_NO"

        fun launch(context: Context, orderNo: String) {
            show(context, getLaunchIntent(context, orderNo))
        }

        fun getLaunchIntent(context: Context, orderNo: String): Intent {
            val intent = Intent(context, RefundActivity::class.java)
            val bundle = Bundle()
            bundle.putString(KEY_ORDER_NO, orderNo)
            intent.putExtras(bundle)
            return intent
        }
    }

    override fun initBundle(bundle: Bundle?): Boolean {
        bundle?.let {
            mOrderNo = it.getString(KEY_ORDER_NO, "")
        }
        return super.initBundle(bundle)
    }

    override fun initWidget(root: View?) {
        super.initWidget(root)
        title_bar.setOnBackClickListener { finish() }
    }

    override fun backable(): Boolean {
        return true
    }

    override fun initData() {
        super.initData()
        val call = AppManager.getSdHttpService().getOrderDetail(mOrderNo)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<OrderDetail>() {
            override fun onFailure(errorResponse: ErrorResponse) {

            }

            override fun onSuccess(response: OrderDetail?) {
                if (response == null) return
                setOrderInfo(response)
            }
        })
    }

    private fun setOrderInfo(order: OrderDetail) {
        LogUtils.d(order)
        ll_order.visibility = View.VISIBLE
        val amount = "%.02f".format(order.amount * 1.0f)
        tv_refund_amount.text = resources.getString(R.string.yuan_, amount)
        tv_refund_way.text = resources.getString(R.string.refund_way, order.refund_way)
        tv_refund_time.text = resources.getString(R.string.refund_time, TimeUtil.formatDate("yyyy/MM/dd HH/mm", order.refundedAtInMillis))
        tv_refund_reason.text = resources.getString(R.string.refund_reason, order.refund_reason)
    }
}
