package com.sumian.sddoctor.patient.presenter

import com.sumian.common.base.BaseViewModel
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.network.response.PatientsResponse
import com.sumian.sddoctor.patient.contract.PatientListContract

class PatientListPresenter private constructor(view: PatientListContract.View) : BaseViewModel(){

    private var mView: PatientListContract.View? = null

    private var mCurrentPageIndex = 1
    private var mIsHaveMore = false
    private var mIsRefresh = false

    init {
        this.mView = view
    }

    companion object {

        fun init(view: PatientListContract.View): PatientListPresenter {
            return PatientListPresenter(view)
        }
    }

     fun getPatients() {

        mView?.showLoading()

        val map = mutableMapOf<String, Any>()
        map["page"] = mCurrentPageIndex
        map["per_page"] = 15

        val call = AppManager.getHttpService().getPatientList(map)

        call.enqueue(object : BaseSdResponseCallback<PatientsResponse>() {

            override fun onSuccess(response: PatientsResponse?) {
                response?.let {
                    if (mIsRefresh) {
                        mView?.onRefreshPatientsSuccess(it.data)
                        mIsRefresh = false
                    } else {
                        mView?.loadMorePatientsSuccess(it.data)
                    }
                    mIsHaveMore = it.meta.pagination.totalPages > mCurrentPageIndex
                    mView?.onHaveMore(isHaveMore = mIsHaveMore)
                    mCurrentPageIndex++
                }
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.getPatientFailed(errorResponse.message)
            }

            override fun onFinish() {
                mView?.dismissLoading()
            }

        })
    }

     fun getNextPatients() {
        if (!mIsHaveMore) return
        getPatients()
    }

     fun refreshPatients() {
        mCurrentPageIndex = 1
        mIsHaveMore = false
        mIsRefresh = true
        getPatients()
    }
}