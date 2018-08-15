package com.sumian.sd.tel.presenter

import com.sumian.common.mvp.IPresenter.Companion.mCalls
import com.sumian.common.network.response.BaseResponseCallback
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.app.AppManager
import com.sumian.sd.network.response.PaginationResponse
import com.sumian.sd.tel.bean.TelBooking
import com.sumian.sd.tel.contract.TelBookingListContract
import retrofit2.Callback

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
        getTelBookingList(mTelBookingType)
    }

    override fun getTelBookingList(telBookingListType: Int) {
        this.mTelBookingType = telBookingListType

        mView?.showLoading()

        val map = mutableMapOf<String, Any>()

        map["page"] = mPageNumber
        map["per_page"] = DEFAULT_PAGES
        map["list_type"] = telBookingListType
        map["include"] = "package.servicePackage"

        val call = AppManager.getHttpService().getTelBookingList(map)
        mCalls.add(call)
        call.enqueue(object : BaseResponseCallback<PaginationResponse<TelBooking>>(), Callback<PaginationResponse<TelBooking>> {
            override fun onFailure(errorResponse: ErrorResponse) {
                mIsRefresh = false
                mView?.onGetTelBookingListFailed(errorResponse.message)
            }

            override fun onSuccess(response: PaginationResponse<TelBooking>?) {
                val data = response?.data
                if (mIsRefresh) {
                    mIsRefresh = false
                    mPageNumber = 1
                    mView?.onRefreshTelBookingListSuccess(data!!)
                } else {
                    mIsRefresh = false
                    mView?.onGetTelBookingListSuccess(data!!)
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
        getTelBookingList(mTelBookingType)
    }

}
