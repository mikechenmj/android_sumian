package com.sumian.sddoctor.booking

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.blankj.utilcode.util.LogUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.base.BaseActivity
import com.sumian.sddoctor.booking.adapter.BookingAdapter
import com.sumian.sddoctor.booking.bean.BookingDayData
import com.sumian.sddoctor.booking.bean.BookingSection
import com.sumian.sddoctor.booking.bean.GetBookingsResponse
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.util.TimeUtil
import kotlinx.android.synthetic.main.activity_booking_management.*
import java.util.*

class BookingManagementActivity : BaseActivity(), BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    val mAdapter = BookingAdapter(null)
    private val mCurrentTime = System.currentTimeMillis()
    var mTopDate: Int = (mCurrentTime / 1000).toInt()
    var mBottomDate: Int = (mCurrentTime / 1000).toInt()

    override fun getContentId(): Int {
        return R.layout.activity_booking_management
    }

    companion object {
        const val PAGE_SIZE = 3
    }

    override fun initWidget() {
        super.initWidget()
        title_bar.setOnBackClickListener { finish() }
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = mAdapter
        mAdapter.setOnLoadMoreListener(this, recycler_view)
        mAdapter.setOnBookingItemClickListener { booking -> BookingDetailActivity.launch(mActivity, booking.id) }
        swipe_refresh.setOnRefreshListener(this)
    }

    override fun initData() {
        super.initData()
        loadMore(true, true)
    }

    override fun onRefresh() {
        loadMore(false, false)
    }


    override fun onLoadMoreRequested() {
        loadMore(true, false)
    }

    private fun loadMore(isIncreaseTime: Boolean, include: Boolean) {
        val time = if (isIncreaseTime) mBottomDate else mTopDate
        val direction = if (isIncreaseTime) 1 else 0
        val isInclude = if (include) 1 else 0
        val call = AppManager.getHttpService().getBookings(time, isInclude, PAGE_SIZE, direction)
        addCall(call)
        call
                .enqueue(object : BaseSdResponseCallback<GetBookingsResponse>() {
                    override fun onSuccess(response: GetBookingsResponse?) {
                        LogUtils.d(response)
                        onGetBookingsResponse(response, isIncreaseTime)
                    }

                    override fun onFailure(errorResponse: ErrorResponse) {
                        LogUtils.d(errorResponse)
                    }

                    override fun onFinish() {
                        LogUtils.d()
                        if (isIncreaseTime) {
                            mAdapter.loadMoreComplete()
                        } else {
                            swipe_refresh.hideRefreshAnim()
                        }
                    }
                })
    }

    private fun onGetBookingsResponse(response: GetBookingsResponse?, isIncreaseTime: Boolean) {
        var list: MutableList<BookingDayData> = mutableListOf()
        if (response == null) {
            if (mAdapter.data.isEmpty()) {
                // 如果今天没数据，则填充一条空数据
                list.add(0, BookingDayData((mCurrentTime / 1000L).toInt(), ArrayList()))
            }
        } else {
            list = response.data
            list.sort()
        }
        // update bottom item date
        // 如果今天没数据，则填充一条空数据
        if (mAdapter.data.isEmpty()) {
            if (list.isEmpty() || !TimeUtil.isInTheSameDay(list[0].date * 1000L, mCurrentTime)) {
                list.add(0, BookingDayData((mCurrentTime / 1000L).toInt(), ArrayList()))
            }
        }
        if (list.isNotEmpty()) {
            if (isIncreaseTime) {
                mBottomDate = list[list.size - 1].date
            } else {
                mTopDate = list[0].date
            }
        }
        // convert and add data
        val sectionList = bookingDayDataToSectionData(list)
        if (isIncreaseTime) {
            mAdapter.addData(sectionList)
        } else {
            mAdapter.addData(0, sectionList)
            if (list.isNotEmpty()) {
                recycler_view.smoothScrollBy(0, -resources.getDimension(R.dimen.space_100).toInt())
            }
        }
        // update adapter load more ability
        if (list.size < PAGE_SIZE) {
            if (isIncreaseTime) {
                mAdapter.setEnableLoadMore(false)
                val footerView = View.inflate(mActivity, R.layout.booking_item_footer_view, null)
                mAdapter.setFooterView(footerView)
            } else {
                swipe_refresh.isEnabled = false
                swipe_refresh.hideRefreshAnim()
                val headerView = View.inflate(mActivity, R.layout.booking_item_header_view, null)
                mAdapter.setHeaderView(headerView)
            }
        }
    }

    private fun bookingDayDataToSectionData(list: List<BookingDayData>): ArrayList<BookingSection> {
        val sectionList = ArrayList<BookingSection>()
        sectionList.reverse()
        for (data in list) {
            val size = data.bookings.size
            val headerSection = BookingSection.createHeaderSection(data.getDateInMillis(), size)
            sectionList.add(headerSection)
            for ((index, booking) in data.bookings.withIndex()) {
                sectionList.add(BookingSection.createItemSection(booking, index == 0, index == size - 1))
            }
        }
        return sectionList
    }
}
