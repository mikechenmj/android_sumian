package com.sumian.sleepdoctor.tel.presenter

import com.sumian.sleepdoctor.app.AppManager
import com.sumian.sleepdoctor.base.SdBasePresenter.mCalls
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback
import com.sumian.sleepdoctor.network.response.PaginationResponse
import com.sumian.sleepdoctor.tel.bean.TelBooking
import com.sumian.sleepdoctor.tel.contract.TelBookingListContract
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
        // map["include"] ="package.servicePackage"
        map["page"] = mPageNumber
        map["per_page"] = DEFAULT_PAGES
        map["list_type"] = telBookingListType

        val call = AppManager.getHttpService().getTelBookingList(map)
        mCalls?.add(call)
        call.enqueue(object : BaseResponseCallback<PaginationResponse<TelBooking>>(), Callback<PaginationResponse<TelBooking>> {
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

            override fun onFailure(code: Int, message: String) {
                mIsRefresh = false
                mView?.onGetTelBookingListFailed(message)
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
