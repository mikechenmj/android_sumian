package com.sumian.sd.buz.coupon.presenter

import com.sumian.common.base.BaseViewModel
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.network.response.PaginationResponseV2
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.coupon.bean.Coupon
import com.sumian.sd.buz.coupon.contract.CouponListContract
import com.sumian.sd.common.network.callback.BaseSdResponseCallback

class CouponListPresenter private constructor(view: CouponListContract.View) : BaseViewModel() {

    private var mView: CouponListContract.View? = null

    private var mPageNumber: Int = 1
    private var mIsRefresh: Boolean = false
    private var mIsGetNext = false

    init {
        this.mView = view
    }

    companion object {

        private const val DEFAULT_PAGES: Int = 15

        @JvmStatic
        fun init(view: CouponListContract.View): CouponListPresenter {
            return CouponListPresenter(view)
        }
    }

     fun getCouponList() {

        mView?.showLoading()

        val map = mutableMapOf<String, Any>()

        map["page"] = mPageNumber
        map["per_page"] = DEFAULT_PAGES

        val call = AppManager.getSdHttpService().getCouponList(map)
         addCall(call)
        call.enqueue(object : BaseSdResponseCallback<PaginationResponseV2<Coupon>>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                mIsRefresh = false
                mView?.onGetCouponListCouponFailed(errorResponse.message)
            }

            override fun onSuccess(response: PaginationResponseV2<Coupon>?) {
                val data = response?.data
                if (mIsRefresh) {
                    mIsRefresh = false
                    mPageNumber = 1
                    mView?.onRefreshCouponListSuccess(data!!)
                } else {
                    mIsRefresh = false
                    if (mIsGetNext) {
                        mIsGetNext = false
                        mView?.onGetNextCouponListSuccess(data!!)
                    } else {
                        mView?.onGetCouponListSuccess(data!!)
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

     fun refreshCouponList() {
        this.mPageNumber = 1
        this.mIsRefresh = true
        this.mIsGetNext = false
        getCouponList()
    }

     fun getNextCouponList() {
        this.mIsGetNext = true
        getCouponList()
    }

}