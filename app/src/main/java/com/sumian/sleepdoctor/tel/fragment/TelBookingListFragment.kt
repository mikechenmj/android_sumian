package com.sumian.sleepdoctor.tel.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.View
import com.sumian.common.base.BasePresenterFragment
import com.sumian.common.base.BaseRecyclerAdapter
import com.sumian.common.helper.ToastHelper
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.base.SdBaseFragment
import com.sumian.sleepdoctor.tel.adpater.TelBookingListAdapter
import com.sumian.sleepdoctor.tel.bean.TelBooking
import com.sumian.sleepdoctor.tel.contract.TelBookingListContract
import com.sumian.sleepdoctor.tel.presenter.TelBookingListPresenter
import kotlinx.android.synthetic.main.fragment_main_advisory_list.*

/**
 * Created by sm
 *
 * on 2018/8/14
 *
 * desc: 电话预约列表, 已完成/未完成
 *
 */
class TelBookingListFragment : BasePresenterFragment<TelBookingListContract.Presenter>(), TelBookingListContract.View, SwipeRefreshLayout.OnRefreshListener, BaseRecyclerAdapter.OnItemClickListener {

    companion object {

        private const val ARGS_TEL_BOOKING_TYPE: String = "com.sumian.app.extras.tel.booking.type"

        fun newInstance(telBookingType: Int = TelBooking.UN_FINISHED_TYPE): Fragment {
            val args = Bundle().apply {
                putInt(ARGS_TEL_BOOKING_TYPE, telBookingType)
            }
            return SdBaseFragment.newInstance(TelBookingListFragment::class.java, args)
        }

    }

    private lateinit var mListAdapter: TelBookingListAdapter

    private var mTelBookingType: Int = TelBooking.UN_FINISHED_TYPE

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
        recycler.adapter = mListAdapter
        mListAdapter.setOnItemClickListener(this)
        empty_error_view.invalidAdvisoryError()
    }

    override fun initData() {
        super.initData()
        this.mPresenter?.getTelBookingList(mTelBookingType)
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
    }

    override fun onRefresh() {
        this.mPresenter?.refreshTelBookingList()
    }

    override fun onItemClick(position: Int, itemId: Long) {

    }

    override fun showLoading() {
        //super.showLoading()
        refresh.showRefreshAnim()
    }

    override fun dismissLoading() {
        //super.dismissLoading()
        refresh.hideRefreshAnim()
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