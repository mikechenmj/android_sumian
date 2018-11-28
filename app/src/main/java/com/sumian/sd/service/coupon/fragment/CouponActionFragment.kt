package com.sumian.sd.service.coupon.fragment

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.text.InputFilter
import android.view.Gravity
import android.view.View
import com.sumian.common.base.BasePresenterFragment
import com.sumian.common.helper.ToastHelper
import com.sumian.hw.utils.UiUtil
import com.sumian.sd.R
import com.sumian.sd.service.coupon.activity.CouponCenterActivity
import com.sumian.sd.service.coupon.contract.CouponActionContract
import com.sumian.sd.service.coupon.presenter.CouponActionPresenter
import kotlinx.android.synthetic.main.fragment_tab_coupon_input.*

/**
 * 兑换码 兑换
 */
class CouponActionFragment : BasePresenterFragment<CouponActionContract.Presenter>(), View.OnClickListener, CouponActionContract.View, CouponCenterActivity.OnKeyBoardCallback {

    companion object {
        @JvmStatic
        fun newInstance(): androidx.fragment.app.Fragment {
            return CouponActionFragment()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_tab_coupon_input
    }

    override fun onInitWidgetBefore() {
        super.onInitWidgetBefore()
        this.mPresenter = CouponActionPresenter.init(this)
    }

    override fun initWidget() {
        super.initWidget()
        bt_coupon.setOnClickListener(this)
        et_coupon_code.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(16))
        (mActivity as CouponCenterActivity).setOnKeyBoardCallback(this)
    }

    override fun onClick(v: View?) {
        val input = et_coupon_code.text.toString().trim()
        mPresenter?.checkCoupon(input)
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