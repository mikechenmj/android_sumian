@file:Suppress("DEPRECATION")

package com.sumian.sd.common.pay.widget

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.widget.adapter.EmptyTextWatcher
import com.sumian.sd.R
import com.sumian.sd.common.pay.bean.PayCouponCode
import com.sumian.sd.common.utils.UiUtil
import com.sumian.sd.common.utils.getString
import kotlinx.android.synthetic.main.lay_pay_calculate_item_view.view.*
import java.util.*

/**
 * Created by sm
 * on 2018/1/22.
 * desc:
 */

class PayCalculateItemView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr), View.OnClickListener {

    companion object {
        private const val CHECK_DELAY = 2000L
    }

    var currentBuyCount = 1
        private set

    var defaultMoney = 0L
        set(defaultMoney) {
            field = defaultMoney
            this.currentMoney = getPayAmount()
            formatMoney(tv_money, this.defaultMoney)
        }
    var currentMoney = 0L
        private set

    private var mOnMoneyChangeCallback: OnMoneyChangeCallback? = null

    private var mDiscountMoney = 0L
    var mIsCouponCodeValid = false
        private set

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
                removeCallbacks(mDelayCheckCouponCodeRunnable)
                showCheckCouponCodeLoading()
                if (!TextUtils.isEmpty(s.toString().trim())) {
                    postDelayed(mDelayCheckCouponCodeRunnable, CHECK_DELAY)
                } else {
                    tv_pay_coupon_money.text = null
                    tv_pay_coupon_code_tips.text = getString(R.string.none_pay_coupon_code)
                    tv_pay_coupon_code_tips.background = null
                    tv_pay_coupon_code_tips.setTextColor(resources.getColor(R.color.t2_color_day))
                    mDiscountMoney = 0
                }
                autoUpdateMoney()
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
        }

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
        autoUpdateMoney()
    }

    private val mDelayCheckCouponCodeRunnable = Runnable {
        val couponCode = getCouponCode()
        if (!TextUtils.isEmpty(couponCode)) {
//            showCheckCouponCodeLoading()
            mOnMoneyChangeCallback?.onCheckCouponCode(couponCode!!)
        }
    }

    @SuppressLint("SetTextI18n")
    fun updateCouponCode(couponCode: PayCouponCode?, errorMessage: String?) {
        if (couponCode == null) {
            mIsCouponCodeValid = false
            this.mDiscountMoney = 0L
            tv_pay_coupon_money.text = null
            tv_pay_coupon_code_tips.text = if (TextUtils.isEmpty(errorMessage)) getString(R.string.none_pay_coupon_code) else errorMessage
            tv_pay_coupon_code_tips.setTextColor(resources.getColor(R.color.t2_color_day))
            tv_pay_coupon_code_tips.background = null
        } else {
            val isDiscountValid = getPayAmount() - couponCode.discount > 0
            val isCouponValid = couponCode.isValid() && isDiscountValid
            if (couponCode.isValid() && !isDiscountValid) {
                ToastUtils.showShort(context.getString(R.string.discount_is_too_much))
            }
            tv_pay_coupon_code_tips.setBackgroundResource(if (isCouponValid) R.drawable.shape_rect_b4_corner_4dp else 0)
            tv_pay_coupon_code_tips.setTextColor(resources.getColor(if (isCouponValid) R.color.white else R.color.t2_color_day))
            this.mDiscountMoney = if (isCouponValid) couponCode.discount else 0L
            tv_pay_coupon_code_tips.text = couponCode.getTips(context, getPayAmount())
            tv_pay_coupon_money.text = if (isCouponValid) String.format(Locale.getDefault(), "%s%.2f%s", "-", couponCode.discount / 100.00f, "元") else null
            mIsCouponCodeValid = isCouponValid
        }
        autoUpdateMoney()
        iv_loading.isVisible = false
    }

    fun closeKeyBoard() {
        UiUtil.closeKeyboard(et_coupon_code)
    }

//    fun updateCouponCodeFailed(invalidMsg: String) {
//        tv_pay_coupon_code_tips.background = null
//        tv_pay_coupon_code_tips.setTextColor(resources.getColor(R.color.t2_color_day))
//        tv_pay_coupon_code_tips.text = invalidMsg
//        tv_pay_coupon_money.text = null
//        iv_loading.isVisible = false
//    }

    fun showCheckCouponCodeLoading() {
        tv_pay_coupon_money.text = null
        tv_pay_coupon_code_tips.background = null
        tv_pay_coupon_code_tips.setTextColor(resources.getColor(R.color.t2_color_day))
        tv_pay_coupon_code_tips.text = tv_pay_coupon_code_tips?.text?.toString()?.trim()
        iv_loading.isVisible = true
    }

    private fun formatMoney(tv: TextView, money: Long) {
        tv.text = String.format(Locale.getDefault(), "%.2f", money / 100.00f)
    }

    private fun autoUpdateMoney() {
        this.currentMoney = getPayAmount() - mDiscountMoney
        mOnMoneyChangeCallback?.onMoneyChange(currentMoney)
        formatMoney(tv_money, this.currentMoney)
    }

    private fun getPayAmount() = defaultMoney * currentBuyCount

    interface OnMoneyChangeCallback {
        fun onMoneyChange(money: Long)
        fun onCheckCouponCode(couponCode: String)
        fun showLoading()
    }

    fun getValidCode(): String? {
        if (mIsCouponCodeValid) {
            return getCouponCode()
        } else {
            return null
        }
    }
}
