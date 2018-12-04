@file:Suppress("MemberVisibilityCanBePrivate", "DEPRECATION")

package com.sumian.sd.pay.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.sumian.sd.R
import kotlinx.android.synthetic.main.lay_pay_divider_item.view.*


/**
 * Created by sm
 * on 2018/1/22.
 * desc:
 */

class PayItemView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr), View.OnClickListener {

    private var mOnPayWayCallback: OnPayWayCallback? = null

    init {
        init(context)
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.PayItemView, defStyleAttr, 0)

        val iconDrawable = a.getDrawable(R.styleable.PayItemView_icon)
        iv_pay_icon.setImageDrawable(iconDrawable)

        val text = a.getString(R.styleable.PayItemView_desc)
        tv_pay_desc.text = text

        val isSelect = a.getBoolean(R.styleable.PayItemView_is_select, false)
        iv_pay_select.tag = if (isSelect) true else null
        iv_pay_select.setImageResource(if (isSelect) R.mipmap.ic_group_pay_selected else R.mipmap.ic_group_pay_unselected)

        a.recycle()

        setOnClickListener(this)
    }

    private fun init(context: Context) {
        gravity = Gravity.CENTER
        orientation = LinearLayout.HORIZONTAL
        View.inflate(context, R.layout.lay_pay_divider_item, this)
        val padding = context.resources.getDimension(R.dimen.space_20).toInt()
        setPadding(padding, 0, padding, 0)
        setBackgroundColor(resources.getColor(R.color.b2_color))
    }

    fun setOnPayWayCallback(onPayWayCallback: OnPayWayCallback) {
        mOnPayWayCallback = onPayWayCallback
    }

    fun select() {
        iv_pay_select.tag = true
        iv_pay_select.setImageResource(R.mipmap.ic_group_pay_selected)
        mOnPayWayCallback?.onSelectPayWay(this)
    }

    fun unSelect() {
        iv_pay_select.tag = null
        iv_pay_select.setImageResource(R.mipmap.ic_group_pay_unselected)
    }

    override fun onClick(v: View) {
        if (iv_pay_select.tag == null) {
            select()
        }
    }


    interface OnPayWayCallback {

        fun onSelectPayWay(v: View)

    }
}
