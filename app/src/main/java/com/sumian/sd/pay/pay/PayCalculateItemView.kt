package com.sumian.sd.pay.pay

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.sumian.sd.R
import kotlinx.android.synthetic.main.lay_pay_calculate_item_view.view.*
import java.util.*

/**
 * Created by sm
 * on 2018/1/22.
 * desc:
 */

class PayCalculateItemView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr), View.OnClickListener {


    var currentBuyCount = 1
        private set

    var defaultMoney = 0.00
        set(defaultMoney) {
            field = defaultMoney
            this.currentMoney = this.defaultMoney * currentBuyCount
            formatMoney(tv_money, this.defaultMoney)
        }
    var currentMoney = 0.00
        private set

    private var mOnMoneyChangeCallback: OnMoneyChangeCallback? = null

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.lay_pay_calculate_item_view, this)
        iv_faq.visibility = View.GONE
        iv_faq.setOnClickListener(this)
        iv_reduce_duration.setOnClickListener(this)
        iv_add_duration.setOnClickListener(this)
        gravity = Gravity.CENTER
        orientation = LinearLayout.VERTICAL
    }

    fun setOnMoneyChangeCallback(onMoneyChangeCallback: OnMoneyChangeCallback) {
        mOnMoneyChangeCallback = onMoneyChangeCallback
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_faq -> {
            }
            R.id.iv_reduce_duration -> if (currentBuyCount == 1) {
                currentBuyCount = 1
            } else {
                currentBuyCount--
            }
            R.id.iv_add_duration -> if (currentBuyCount < 99) {
                currentBuyCount++
            }
            else -> {
            }
        }//                @SuppressLint("InflateParams") View rootView = LayoutInflater.from(v.getContext()).inflate(R.layout.lay_pop_pay_faq, null, false);
        //
        //                CustomPopWindow popWindow = new CustomPopWindow.PopupWindowBuilder(v.getContext())
        //                        .setView(rootView)//显示的布局，还可以通过设置一个View
        //                        //     .size(600,400) //设置显示的大小，不设置就默认包裹内容
        //                        .setFocusable(true)//是否获取焦点，默认为ture
        //                        .setOutsideTouchable(true)//是否PopupWindow 以外触摸dissmiss
        //                        .create()//创建PopupWindow
        //                        .showAsDropDown(mIvPayFaq, -3 * (mIvPayFaq.getWidth()), (int) (-4.4 * mIvPayFaq.getHeight()), Gravity.TOP | Gravity.CENTER);//显示PopupWindow
        //
        //                v.postDelayed(popWindow::dismiss, 3000);
        //                rootView.setOnClickListener(v1 -> popWindow.dismiss());

        if (currentBuyCount > 1) {
            iv_reduce_duration.isEnabled = true
            iv_reduce_duration.setImageResource(R.mipmap.ic_group_pay_btn_plus)
        } else {
            iv_reduce_duration.isEnabled = false
            iv_reduce_duration.setImageResource(R.mipmap.ic_group_pay_btn_plus_disabled)
        }

        if (currentBuyCount < 99) {
            iv_add_duration.isEnabled = true
            iv_add_duration.setImageResource(R.mipmap.ic_group_pay_btn_minus)
        } else {
            iv_add_duration.isEnabled = false
            iv_add_duration.setImageResource(R.mipmap.ic_group_pay_btn_minus_disabled)
        }

        tv_duration.text = currentBuyCount.toString()
        currentMoney = defaultMoney * currentBuyCount

        if (mOnMoneyChangeCallback != null) {
            mOnMoneyChangeCallback!!.onMoneyChange(currentMoney)
        }
        updateMoney(currentMoney)
    }

    private fun updateMoney(currentMoney: Double) {
        this.currentMoney = currentMoney
        formatMoney(tv_money, this.currentMoney)
    }

    private fun formatMoney(tv: TextView, money: Double) {
        tv.text = String.format(Locale.getDefault(), "%.2f", money / 100.00f)
    }

    interface OnMoneyChangeCallback {
        fun onMoneyChange(money: Double)
    }
}
