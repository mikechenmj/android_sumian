package com.sumian.sd.service.coupon.presenter

import com.sumian.common.mvp.IPresenter.Companion.mCalls
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.network.response.PaginationResponseV2
import com.sumian.sd.app.AppManager
import com.sumian.sd.network.callback.BaseSdResponseCallback
import com.sumian.sd.service.coupon.bean.Coupon
import com.sumian.sd.service.coupon.contract.CouponListContract

class CouponListPresenter private constructor(view: CouponListContract.View) : CouponListContract.Presenter {

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
        fun init(view: CouponListContract.View): CouponListContract.Presenter {
            return CouponListPresenter(view)
        }
    }

    override fun getCouponList() {

        mView?.showLoading()

        val map = mutableMapOf<String, Any>()

        map["page"] = mPageNumber
        map["per_page"] = DEFAULT_PAGES

        val call = AppManager.getSdHttpService().getCouponList(map)
        mCalls.add(call)
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

    override fun refreshCouponList() {
        this.mPageNumber = 1
        this.mIsRefresh = true
        this.mIsGetNext = false
        getCouponList()
    }

    override fun getNextCouponList() {
        this.mIsGetNext = true
        getCouponList()
    }

}