package com.sumian.sd.service.coupon.fragment

import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.View
import com.sumian.common.base.BasePresenterFragment
import com.sumian.common.base.BaseRecyclerAdapter
import com.sumian.common.helper.ToastHelper
import com.sumian.sd.R
import com.sumian.sd.service.coupon.adpater.CouponListAdapter
import com.sumian.sd.service.coupon.bean.Coupon
import com.sumian.sd.service.coupon.contract.CouponListContract
import com.sumian.sd.service.coupon.presenter.CouponListPresenter
import com.sumian.sd.widget.LoadMoreRecyclerView
import kotlinx.android.synthetic.main.fragment_main_advisory_list.*

/**
 * Created by sm
 *
 * on 2018/8/14
 *
 * desc: 兑换记录列表
 *
 */
class CouponListFragment : BasePresenterFragment<CouponListContract.Presenter>(), CouponListContract.View, SwipeRefreshLayout.OnRefreshListener, BaseRecyclerAdapter.OnItemClickListener, LoadMoreRecyclerView.OnLoadCallback {

    companion object {

        @JvmStatic
        fun newInstance(): Fragment {
            return CouponListFragment()
        }

    }

    private val mListAdapter: CouponListAdapter  by lazy {
        CouponListAdapter(context!!)
    }

    private var mIsRefresh = false

    override fun getLayoutId(): Int {
        return R.layout.fragment_main_advisory_list
    }

    override fun onInitWidgetBefore() {
        super.onInitWidgetBefore()
        this.mPresenter = CouponListPresenter.init(this)
    }

    override fun initWidget() {
        super.initWidget()
        refresh.setOnRefreshListener(this)
        recycler.setOnLoadCallback(this)
        recycler.adapter = mListAdapter
        recycler.itemAnimator = null
        recycler.layoutManager = LinearLayoutManager(context)
        mListAdapter.setOnItemClickListener(this)
        empty_error_view.invalidCouponError()
        empty_error_view.isEnabled = false
    }

    override fun initData() {
        super.initData()
        mIsRefresh = true
        this.mPresenter?.getCouponList()
    }

    override fun onResume() {
        super.onResume()
        if (!mIsRefresh) {
            onRefresh()
        }
    }

    override fun onStop() {
        super.onStop()
        mIsRefresh = false
    }

    override fun onRefresh() {
        mIsRefresh = true
        refresh.showRefreshAnim()
        this.mPresenter?.refreshCouponList()
    }

    override fun loadMore() {
        super.loadMore()
        mPresenter?.getNextCouponList()
    }

    override fun onItemClick(position: Int, itemId: Long) {
        //val coupon = this.mListAdapter.getItem(position)
    }

    override fun showLoading() {
        //super.showLoading()
    }

    override fun dismissLoading() {
        //super.dismissLoading()
        refresh.hideRefreshAnim()
        mIsRefresh = false
    }

    override fun onGetCouponListSuccess(couponList: List<Coupon>) {
        if (couponList.isEmpty()) {
            empty_error_view.invalidAdvisoryError()
            recycler.visibility = View.GONE
        } else {
            mListAdapter.resetItem(couponList)
            recycler.visibility = View.VISIBLE
            empty_error_view.hide()
        }

    }

    override fun onGetCouponListCouponFailed(error: String) {
        ToastHelper.show(activity, error, Gravity.CENTER)
    }

    override fun onGetNextCouponListSuccess(couponList: List<Coupon>) {
        if (couponList.isEmpty()) return
        mListAdapter.addAll(couponList)
        recycler.visibility = View.VISIBLE
        empty_error_view.hide()
    }

    override fun onRefreshCouponListSuccess(couponList: List<Coupon>) {
        mListAdapter.clear()
        if (couponList.isEmpty()) {
            empty_error_view.invalidAdvisoryError()
        } else {
            mListAdapter.resetItem(couponList)
            recycler.visibility = View.VISIBLE
            empty_error_view.hide()
        }
    }
}