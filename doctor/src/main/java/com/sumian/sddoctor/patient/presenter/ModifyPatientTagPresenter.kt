package com.sumian.sddoctor.patient.presenter

import com.sumian.common.base.BaseViewModel
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.patient.bean.Patient
import com.sumian.sddoctor.patient.contract.ModifyPatientTagContract

/**
 * Created by dq
 *
 * on 2018/8/30
 *
 * desc:
 */
class ModifyPatientTagPresenter private constructor(view: ModifyPatientTagContract.View) : BaseViewModel() {

    private var mView: ModifyPatientTagContract.View? = null

    init {
        this.mView = view
    }

    companion object {
        fun init(view: ModifyPatientTagContract.View): ModifyPatientTagPresenter {
            return ModifyPatientTagPresenter(view)
        }
    }

     fun getPatient(patientId: Int) {

        mView?.showLoading()

        val call = AppManager.getHttpService().getPatientDetail(patientId)
         addCall(call)
        call.enqueue(object : BaseSdResponseCallback<Patient>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.onGetPatientFailed(error = errorResponse.message)
            }

            override fun onSuccess(response: Patient?) {
                mView?.onGetPatientSuccess(response!!)
            }

            override fun onFinish() {
                super.onFinish()
                mView?.dismissLoading()
            }
        })
    }

     fun consultedPatient(patientId: Int, consulted: Int) {

        mView?.showLoading()

        val call = AppManager.getHttpService().consulted(patientId, consulted)
         addCall(call)

        call.enqueue(object : BaseSdResponseCallback<Patient>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.onConsultedFailed(error = errorResponse.message)
            }

            override fun onSuccess(response: Patient?) {
                mView?.onConsultedSuccess(response!!)
            }

            override fun onFinish() {
                super.onFinish()
                mView?.dismissLoading()
            }

        })


    }
}