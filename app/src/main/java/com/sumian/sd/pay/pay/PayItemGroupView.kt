package com.sumian.sd.pay.pay

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout

import com.sumian.sd.R
import kotlinx.android.synthetic.main.lay_pay_item_group.view.*


/**
 * Created by sm
 * on 2018/1/22.
 * desc:
 */

class PayItemGroupView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr), PayItemView.OnPayWayCallback {


    private var mOnSelectPayWayListener: OnSelectPayWayListener? = null

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.lay_pay_item_group, this)
        gravity = Gravity.CENTER
        orientation = LinearLayout.VERTICAL
        wechat_pay_item.setOnPayWayCallback(this)
        alipay_pay_item.setOnPayWayCallback(this)
    }

    fun setOnSelectPayWayListener(onSelectPayWayListener: OnSelectPayWayListener) {
        mOnSelectPayWayListener = onSelectPayWayListener
    }

    override fun onSelectPayWay(v: View) {
        when (v.id) {
            R.id.wechat_pay_item -> {
                alipay_pay_item.unSelect()
                if (mOnSelectPayWayListener != null) {
                    mOnSelectPayWayListener!!.onSelectWechatPayWay()
                }
            }
            R.id.alipay_pay_item -> {
                wechat_pay_item.unSelect()
                if (mOnSelectPayWayListener != null) {
                    mOnSelectPayWayListener!!.onSelectAlipayWay()
                }
            }
            else -> {
            }
        }
    }

    interface OnSelectPayWayListener {

        fun onSelectWechatPayWay()

        fun onSelectAlipayWay()
    }
}
