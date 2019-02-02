package com.sumian.sd.buz.coupon.fragment

import android.content.Intent
import android.text.InputFilter
import android.view.Gravity
import android.view.View
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.KeyboardUtils
import com.sumian.common.base.BaseViewModelFragment
import com.sumian.common.helper.ToastHelper
import com.sumian.sd.R
import com.sumian.sd.buz.coupon.activity.CouponCenterActivity
import com.sumian.sd.buz.coupon.contract.CouponActionContract
import com.sumian.sd.buz.coupon.presenter.CouponActionPresenter
import com.sumian.sd.common.utils.UiUtil
import kotlinx.android.synthetic.main.fragment_tab_coupon_input.*

/**
 * 兑换码 兑换
 */
class CouponActionFragment : BaseViewModelFragment<CouponActionPresenter>(), View.OnClickListener, CouponActionContract.View, CouponCenterActivity.OnKeyBoardCallback {

    companion object {
        @JvmStatic
        fun newInstance(): Fragment {
            return CouponActionFragment()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_tab_coupon_input
    }

    override fun onInitWidgetBefore() {
        super.onInitWidgetBefore()
        this.mViewModel = CouponActionPresenter.init(this)
    }

    override fun initWidget() {
        super.initWidget()
        bt_coupon.setOnClickListener(this)
        et_coupon_code.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(16))
        (mActivity as CouponCenterActivity).setOnKeyBoardCallback(this)
        vg_coupon_fragment_root.setOnClickListener { KeyboardUtils.hideSoftInput(activity) }
    }

    override fun onClick(v: View?) {
        val input = et_coupon_code.text.toString().trim()
        mViewModel?.checkCoupon(input)
    }

    override fun onInputCouponSuccess() {
        onInputCouponFailed(getString(R.string.coupon_success))
        (mActivity as CouponCenterActivity).switchTab(1)
        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(activity!!).sendBroadcast(Intent(CouponListFragment.AUTO_REFRESH_ACTION))
    }

    override fun onInputCouponFailed(error: String) {
        ToastHelper.show(activity, error, Gravity.CENTER)
    }

    override fun onCheckFailed(error: String) {
        onInputCouponFailed(error)
    }

    override fun closeKeyBoard() {
        UiUtil.closeKeyboard(et_coupon_code)
    }

    override fun showKeyBoard() {
        UiUtil.showSoftKeyboard(et_coupon_code)
    }
}