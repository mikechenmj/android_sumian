@file:Suppress("DEPRECATION")

package com.sumian.sd.pay.widget

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.qmuiteam.qmui.util.QMUISpanHelper
import com.sumian.common.widget.adapter.EmptyTextWatcher
import com.sumian.hw.utils.UiUtil
import com.sumian.sd.R
import com.sumian.sd.pay.bean.PayCouponCode
import kotlinx.android.synthetic.main.lay_pay_calculate_item_view.view.*
import java.util.*

/**
 * Created by sm
 * on 2018/1/22.
 * desc:
 */

class PayCalculateItemView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr), View.OnClickListener {

    companion object {

        private const val CHECK_DELAY = 100
    }

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

    private var mTimes: Long = 0

    private var mDiscountMoney = 0.00

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        gravity = Gravity.CENTER
        orientation = LinearLayout.VERTICAL
        View.inflate(context, R.layout.lay_pay_calculate_item_view, this)
        iv_faq.visibility = View.GONE
        iv_faq.setOnClickListener(this)
        iv_reduce_duration.setOnClickListener(this)
        iv_add_duration.setOnClickListener(this)
        et_coupon_code.addTextChangedListener(object : EmptyTextWatcher() {

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                if (!TextUtils.isEmpty(s.toString().trim())) {
                    if (System.currentTimeMillis() - mTimes >= CHECK_DELAY) {
                        showCheckCouponCodeLoading()
                        mOnMoneyChangeCallback?.onCheckCouponCode(s.toString().trim())
                    }
                    mTimes = System.currentTimeMillis()
                }
            }
        })
    }

    fun getCouponCode(): String? = et_coupon_code.text?.toString()?.trim()

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
        currentMoney = defaultMoney * currentBuyCount - mDiscountMoney

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

    @SuppressLint("SetTextI18n")
    fun updateCouponCodeTips(couponCode: PayCouponCode?) {
        if (couponCode == null) {
            this.mDiscountMoney = 0.00
            tv_pay_coupon_money.text = null
            tv_pay_coupon_code_tips.text = "此优惠码无效"
        } else {
            tv_pay_coupon_code_tips.text = couponCode.tips()
            if (couponCode.status == 1) {
                this.mDiscountMoney = couponCode.discount
                tv_pay_coupon_money.text = "-${couponCode.discount / 100.00f}元"
            } else {
                this.mDiscountMoney = 0.00
                tv_pay_coupon_money.text = null
            }
        }
    }

    fun closeKeyBoard() {
        UiUtil.closeKeyboard(et_coupon_code)
    }

    fun updateCouponCodeFailed(invalidMsg: String) {
        tv_pay_coupon_code_tips.text = invalidMsg
        tv_pay_coupon_money.text = null
    }

    fun showCheckCouponCodeLoading() {
        val loadingText = QMUISpanHelper.generateSideIconText(false, resources.getDimensionPixelOffset(R.dimen.space_10), "暂未使用优惠", resources.getDrawable(R.drawable.pay_coupon_code_loading_animation))
        tv_pay_coupon_code_tips.text = loadingText
        tv_pay_coupon_money.text = null
    }

    interface OnMoneyChangeCallback {
        fun onMoneyChange(money: Double)

        fun onCheckCouponCode(couponCode: String)
    }
}
