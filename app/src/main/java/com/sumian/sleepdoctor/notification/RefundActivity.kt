package com.sumian.sleepdoctor.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.base.BaseActivity
import com.sumian.sleepdoctor.base.BasePresenter

class RefundActivity : BaseActivity<BasePresenter<Any>>() {
    private var mOrderNo: Long = 0

    companion object {
        private const val KEY_ORDER_NO = "KEY_ORDER_NO"

        fun launch(context: Context, orderNo: Long) {
            show(context, getLaunchIntent(context, orderNo))
        }

        fun getLaunchIntent(context: Context, orderNo: Long): Intent {
            val intent = Intent(context, RefundActivity::class.java)
            val bundle = Bundle()
            bundle.putLong(KEY_ORDER_NO, orderNo)
            intent.putExtras(bundle)
            return intent
        }
    }

    override fun initBundle(bundle: Bundle?): Boolean {
        if (bundle != null) {
            mOrderNo = bundle.getLong(KEY_ORDER_NO)
        }
        return super.initBundle(bundle)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_refund
    }


}
