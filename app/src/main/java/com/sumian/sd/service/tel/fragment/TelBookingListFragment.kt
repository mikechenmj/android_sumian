package com.sumian.sd.service.tel.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.sumian.common.base.BasePresenterFragment
import com.sumian.common.base.BaseRecyclerAdapter
import com.sumian.common.helper.ToastHelper
import com.sumian.common.widget.recycler.LoadMoreRecyclerView
import com.sumian.sd.R
import com.sumian.sd.base.SdBaseFragment
import com.sumian.sd.service.tel.activity.TelBookingDetailActivity
import com.sumian.sd.service.tel.activity.TelBookingPublishActivity
import com.sumian.sd.service.tel.adpater.TelBookingListAdapter
import com.sumian.sd.service.tel.bean.TelBooking
import com.sumian.sd.service.tel.contract.TelBookingListContract
import com.sumian.sd.service.tel.presenter.TelBookingListPresenter
import kotlinx.android.synthetic.main.fragment_main_advisory_list.*

/**
 * Created by sm
 *
 * on 2018/8/14
 *
 * desc: 电话预约列表, 已完成/未完成
 *
 */
class TelBookingListFragment : BasePresenterFragment<TelBookingListContract.Presenter>(), TelBookingListContract.View, SwipeRefreshLayout.OnRefreshListener, BaseRecyclerAdapter.OnItemClickListener, LoadMoreRecyclerView.OnLoadCallback {

    companion object {

        private const val ARGS_TEL_BOOKING_TYPE: String = "com.sumian.app.extras.tel.booking.type"

        fun newInstance(telBookingType: Int = TelBooking.UN_FINISHED_TYPE): androidx.fragment.app.Fragment {
            val args = Bundle().apply {
                putInt(ARGS_TEL_BOOKING_TYPE, telBookingType)
            }
            return SdBaseFragment.newInstance(TelBookingListFragment::class.java, args)
        }

    }

    private lateinit var mListAdapter: TelBookingListAdapter

    private var mTelBookingType: Int = TelBooking.UN_FINISHED_TYPE

    private var mIsRefresh = false

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        this.mTelBookingType = bundle.getInt(ARGS_TEL_BOOKING_TYPE, TelBooking.UN_FINISHED_TYPE)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_main_advisory_list
    }

    override fun onInitWidgetBefore() {
        super.onInitWidgetBefore()
        this.mPresenter = TelBookingListPresenter.init(this)
    }

    override fun initWidget() {
        super.initWidget()
        refresh.setOnRefreshListener(this)
        recycler.layoutManager = LinearLayoutManager(context)
        mListAdapter = TelBookingListAdapter(context!!)
        recycler.setOnLoadCallback(this)
        recycler.adapter = mListAdapter
        mListAdapter.setOnItemClickListener(this)
        empty_error_view.invalidAdvisoryError()
        empty_error_view.isEnabled = false
    }

    override fun initData() {
        super.initData()
        mIsRefresh = true
        this.mPresenter?.getTelBookingList(mTelBookingType)
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
        this.mPresenter?.refreshTelBookingList()
        refresh.showRefreshAnim()
    }

    override fun loadMore() {
        super.loadMore()
        mPresenter?.getNextTelBookingList()
    }

    override fun onItemClick(position: Int, itemId: Long) {
        val telBooking = this.mListAdapter.getItem(position)

        if (telBooking.status == 9) {//未使用,跳转到可提交的预约电话详情
            TelBookingPublishActivity.show(telBooking)
        } else {//跳转到预约电话清单详情
            TelBookingDetailActivity.show(telBookingId = telBooking.id)
        }
    }

    override fun showLoading() {
        //super.showLoading()
    }

    override fun dismissLoading() {
        //super.dismissLoading()
        refresh.hideRefreshAnim()
        mIsRefresh = false
    }

    override fun onGetTelBookingListSuccess(telBookingList: List<TelBooking>) {
        if (telBookingList.isEmpty()) {
            empty_error_view.invalidAdvisoryError()
            recycler.visibility = View.GONE
        } else {
            mListAdapter.resetItem(telBookingList)
            recycler.visibility = View.VISIBLE
            empty_error_view.hide()
        }

    }

    override fun onGetTelBookingListFailed(error: String) {
        ToastHelper.show(activity, error, Gravity.CENTER)
    }

    override fun onGetNextTelBookingListSuccess(telBookingList: List<TelBooking>) {
        if (telBookingList.isEmpty()) return
        mListAdapter.addAll(telBookingList)
        recycler.visibility = View.VISIBLE
        empty_error_view.hide()
    }

    override fun onRefreshTelBookingListSuccess(telBookingList: List<TelBooking>) {
        mListAdapter.clear()
        if (telBookingList.isEmpty()) {
            empty_error_view.invalidAdvisoryError()
        } else {
            mListAdapter.resetItem(telBookingList)
            recycler.visibility = View.VISIBLE
            empty_error_view.hide()
        }
    }
}