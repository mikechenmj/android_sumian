package com.sumian.sddoctor.patient.presenter

import com.sumian.sddoctor.patient.contract.PatientRecommendContract

/**
 * Created by sm
 *
 * on 2018/8/3
 *
 * desc:
 *
 */
class PatientRecommendPresenter private constructor(view: PatientRecommendContract.View) : PatientRecommendContract.Presenter {

    private var mView: PatientRecommendContract.View? = null

    init {
        this.mView = view
    }

    companion object {

        fun init(view: PatientRecommendContract.View): PatientRecommendContract.Presenter {
            return PatientRecommendPresenter(view)
        }
    }

//    override fun getPatientRecommendStatus(patientId: Int) {
//
//        mView?.showLoading()
//
//        val call = AppManager.getHttpService().getPatientRecommendation(patientId = patientId)
//
//        call.enqueue(object : BaseSdResponseCallback<PatientRecommendation>() {
//
//            override fun onSuccess(response: PatientRecommendation?) {
//                response?.let {
//                    mView?.onGetPatientRecommendSuccess(recommend = response.status)
//                }
//            }
//
//            override fun onFailure(errorResponse: ErrorResponse) {
//                mView?.onGetPatientRecommendFailed(error = errorResponse.message)
//            }
//
//            override fun onFinish() {
//                super.onFinish()
//                mView?.dismissLoading()
//            }
//
//        })
//    }
//
//    override fun bookRecommendPatient(patientId: Int) {
//
//        mView?.showLoading()
//
//        val call = AppManager.getHttpService().recommendPatient(patientId = patientId)
//
//        call.enqueue(object : BaseSdResponseCallback<PatientRecommendation>() {
//
//            override fun onSuccess(response: PatientRecommendation?) {
//                mView?.onPatientRecommendSuccess(response?.status!!)
//            }
//
//            override fun onFailure(errorResponse: ErrorResponse) {
//                mView?.onPatientRecommendFailed(error = errorResponse.message)
//            }
//
//            override fun onFinish() {
//                super.onFinish()
//                mView?.dismissLoading()
//            }
//
//        })
//
//    }


}