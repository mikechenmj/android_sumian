package com.sumian.sleepdoctor.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.LogUtils
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.app.AppManager
import com.sumian.sleepdoctor.base.BaseActivity
import com.sumian.sleepdoctor.base.BasePresenter
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback
import com.sumian.sleepdoctor.network.response.ErrorResponse
import com.sumian.sleepdoctor.order.OrderDetailV2
import com.sumian.sleepdoctor.utils.TimeUtil
import kotlinx.android.synthetic.main.activity_refund.*

class RefundActivity : BaseActivity<BasePresenter<Any>>() {
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
        if (bundle != null) {
            mOrderNo = bundle.getString(KEY_ORDER_NO)
        }
        return super.initBundle(bundle)
    }

    override fun initWidget(root: View?) {
        super.initWidget(root)
        title_bar.setOnBackClickListener({ finish() })
    }

    override fun backable(): Boolean {
        return true
    }

    override fun initData() {
        super.initData()
        AppManager.getHttpService().getOrderDetailV2(mOrderNo).enqueue(object : BaseResponseCallback<OrderDetailV2>() {
            override fun onSuccess(response: OrderDetailV2?) {
                if (response == null) return
                setOrderInfo(response)
            }

            override fun onFailure(errorResponse: ErrorResponse?) {
            }
        })
    }

    private fun setOrderInfo(order: OrderDetailV2) {
        LogUtils.d(order)
        ll_order.visibility = View.VISIBLE
        val amount = "%.02f".format(order.amount * 1.0f)
        tv_refund_amount.text = resources.getString(R.string.yuan_, amount)
        tv_refund_way.text = resources.getString(R.string.refund_way, order.refund_way)
        tv_refund_time.text = resources.getString(R.string.refund_time, TimeUtil.formatDate("yyyy/MM/dd HH/mm", order.refundedAtInMillis))
        tv_refund_reason.text = resources.getString(R.string.refund_reason, order.refund_reason)
    }
}
