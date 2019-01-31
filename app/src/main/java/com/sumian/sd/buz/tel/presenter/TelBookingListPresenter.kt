package com.sumian.sd.buz.tel.presenter

import com.sumian.common.mvp.IPresenter.Companion.mCalls
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.network.response.PaginationResponseV2
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.tel.bean.TelBooking
import com.sumian.sd.buz.tel.contract.TelBookingListContract
import com.sumian.sd.common.network.callback.BaseSdResponseCallback

/**
 *
 *Created by sm
 * on 2018/6/4 16:01
 * desc:
 **/
class TelBookingListPresenter private constructor(view: TelBookingListContract.View) : TelBookingListContract.Presenter {

    private var mView: TelBookingListContract.View? = null

    private var mPageNumber: Int = 1
    private var mTelBookingType = TelBooking.UN_FINISHED_TYPE
    private var mIsRefresh: Boolean = false
    private var mIsGetNext = false

    init {
        this.mView = view
    }

    companion object {

        const val DEFAULT_PAGES: Int = 15

        fun init(view: TelBookingListContract.View): TelBookingListContract.Presenter {
            return TelBookingListPresenter(view)
        }
    }

    override fun refreshTelBookingList() {
        this.mPageNumber = 1
        this.mIsRefresh = true
        this.mIsGetNext = false
        getTelBookingList(mTelBookingType)
    }

    override fun getTelBookingList(telBookingListType: Int) {
        this.mTelBookingType = telBookingListType

        mView?.showLoading()

        val map = mutableMapOf<String, Any>()

        map["page"] = mPageNumber
        map["per_page"] = DEFAULT_PAGES
        map["list_type"] = telBookingListType
        map["include"] = "package.servicePackage.service"

        val call = AppManager.getSdHttpService().getTelBookingList(map)
        mCalls.add(call)
        call.enqueue(object : BaseSdResponseCallback<PaginationResponseV2<TelBooking>>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                mIsRefresh = false
                mView?.onGetTelBookingListFailed(errorResponse.message)
            }

            override fun onSuccess(response: PaginationResponseV2<TelBooking>?) {
                val data = response?.data
                if (mIsRefresh) {
                    mIsRefresh = false
                    mPageNumber = 1
                    mView?.onRefreshTelBookingListSuccess(data!!)
                } else {
                    mIsRefresh = false
                    if (mIsGetNext) {
                        mIsGetNext = false
                        mView?.onGetNextTelBookingListSuccess(data!!)
                    } else {
                        mView?.onGetTelBookingListSuccess(data!!)
                    }
                }
                if (data != null && !data.isEmpty()) {
                    mPageNumber++
                }
            }

            override fun onFinish() {
                mView?.dismissLoading()
            }

        })
    }

    override fun getNextTelBookingList() {
        this.mIsGetNext = true
        getTelBookingList(mTelBookingType)
    }

}
