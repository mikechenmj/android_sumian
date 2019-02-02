package com.sumian.sd.buz.coupon.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.Gravity
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.sumian.common.base.BaseRecyclerAdapter
import com.sumian.common.base.BaseViewModelFragment
import com.sumian.common.helper.ToastHelper
import com.sumian.common.widget.recycler.LoadMoreRecyclerView
import com.sumian.sd.R
import com.sumian.sd.buz.coupon.adpater.CouponListAdapter
import com.sumian.sd.buz.coupon.bean.Coupon
import com.sumian.sd.buz.coupon.contract.CouponListContract
import com.sumian.sd.buz.coupon.presenter.CouponListPresenter
import kotlinx.android.synthetic.main.fragment_main_advisory_list.*

/**
 * Created by sm
 *
 * on 2018/8/14
 *
 * desc: 兑换记录列表
 *
 */
class CouponListFragment : BaseViewModelFragment<CouponListPresenter>(), CouponListContract.View, SwipeRefreshLayout.OnRefreshListener, BaseRecyclerAdapter.OnItemClickListener, LoadMoreRecyclerView.OnLoadCallback {

    companion object {

        const val AUTO_REFRESH_ACTION = "com.sumian.sd.action.auto_refresh"

        @JvmStatic
        fun newInstance(): androidx.fragment.app.Fragment {
            return CouponListFragment()
        }

    }

    private val mListAdapter: CouponListAdapter  by lazy {
        CouponListAdapter(context!!)
    }

    private val mDataBroadcastReceiver: BroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                when (intent.action) {
                    AUTO_REFRESH_ACTION -> {
                        onRefresh()
                    }
                }
            }
        }
    }

    private var mIsRefresh = false

    override fun getLayoutId(): Int {
        return R.layout.fragment_main_advisory_list
    }

    override fun onInitWidgetBefore() {
        super.onInitWidgetBefore()
        this.mViewModel = CouponListPresenter.init(this)
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
        this.mViewModel?.getCouponList()
        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(activity!!).registerReceiver(mDataBroadcastReceiver, IntentFilter(AUTO_REFRESH_ACTION))
    }

    override fun onRelease() {
        super.onRelease()
        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(mDataBroadcastReceiver)
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
        this.mViewModel?.refreshCouponList()
    }

    override fun loadMore() {
        super.loadMore()
        mViewModel?.getNextCouponList()
    }

    override fun onItemClick(position: Int, itemId: Long) {
        //val coupon = this.mListAdapter.getItem(position)
    }

    override fun showLoading() {
        //super.showLoading()
        refresh?.showRefreshAnim()
    }

    override fun dismissLoading() {
        //super.dismissLoading()
        refresh?.hideRefreshAnim()
        mIsRefresh = false
    }

    override fun onGetCouponListSuccess(couponList: List<Coupon>) {
        if (couponList.isEmpty()) {
            empty_error_view.invalidCouponError()
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
            empty_error_view.invalidCouponError()
        } else {
            mListAdapter.resetItem(couponList)
            recycler.visibility = View.VISIBLE
            empty_error_view.hide()
        }
    }
}