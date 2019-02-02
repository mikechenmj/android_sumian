package com.sumian.sddoctor.service.evaluation.presenter

import com.sumian.common.base.BaseViewModel
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.service.evaluation.bean.EvaluationResponse
import com.sumian.sddoctor.service.evaluation.bean.WeekEvaluation
import com.sumian.sddoctor.service.evaluation.contract.WeekEvaluationListContract

/**
 *
 *Created by sm
 * on 2018/6/4 16:01
 * desc:
 **/
class WeekEvaluationListPresenter private constructor(view: WeekEvaluationListContract.View) : BaseViewModel() {

    companion object {

        private const val DEFAULT_PAGES: Int = 10

        fun init(view: WeekEvaluationListContract.View): WeekEvaluationListPresenter {
            return WeekEvaluationListPresenter(view)
        }
    }

    private var mView: WeekEvaluationListContract.View? = null

    private var mEvaluationType = WeekEvaluation.ALL_TYPE

    private var mCurrentPageIndex = 1
    private var mIsHaveMore = false
    private var mIsRefresh = false

    init {
        this.mView = view
    }

     fun refreshEvaluationList() {
        mCurrentPageIndex = 1
        mIsHaveMore = false
        mIsRefresh = true
        getEvaluationList(mEvaluationType)
    }

     fun getEvaluationList(WeekEvaluationType: String) {
        this.mEvaluationType = WeekEvaluationType

        mView?.showLoading()

        val map = mutableMapOf<String, Any>()

        map["page"] = mCurrentPageIndex
        map["per_page"] = DEFAULT_PAGES
        map["status_type"] = WeekEvaluationType

        map["include"] = "user,doctor"

        val call = AppManager.getHttpService().getDoctorDiaryEvaluations(map)
         addCall(call)
        call.enqueue(object : BaseSdResponseCallback<EvaluationResponse>() {

            override fun onFailure(errorResponse: ErrorResponse) {
                mIsRefresh = false
                mView?.onGetEvaluationListFailed(errorResponse.message)
            }

            override fun onSuccess(response: EvaluationResponse?) {

                if (mIsRefresh) {
                    mIsRefresh = false
                    mView?.onRefreshEvaluationListSuccess(response?.data!!)
                } else {
                    mIsRefresh = false
                    mView?.onGetEvaluationListSuccess(response?.data!!)
                }

                mIsHaveMore = response!!.meta.pagination.totalPages > mCurrentPageIndex
                mView?.onHaveMore(isHaveMore = mIsHaveMore)
            }

            override fun onFinish() {
                mView?.dismissLoading()
            }

        })
    }

     fun getNextEvaluationList() {
        if (!mIsHaveMore) return
        mCurrentPageIndex++
        getEvaluationList(mEvaluationType)
    }

}
