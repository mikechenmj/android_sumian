package com.sumian.sddoctor.account.presenter

import com.sumian.common.base.BaseViewModel
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sddoctor.account.activity.ModifyDoctorInfoActivity
import com.sumian.sddoctor.account.activity.ModifyDoctorInfoActivity.Companion.MODIFY_TYPE_DEPARTMENT
import com.sumian.sddoctor.account.activity.ModifyDoctorInfoActivity.Companion.MODIFY_TYPE_HOSPITAL
import com.sumian.sddoctor.account.activity.ModifyDoctorInfoActivity.Companion.MODIFY_TYPE_JOB_TITLE
import com.sumian.sddoctor.account.activity.ModifyDoctorInfoActivity.Companion.MODIFY_TYPE_NAME
import com.sumian.sddoctor.account.contract.AccountContract
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.login.login.bean.DoctorInfo
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import retrofit2.Call

class AccountPresenter private constructor(view: AccountContract.View?) : BaseViewModel() {

    private var mView: AccountContract.View? = null

    private var mCall: Call<DoctorInfo>? = null

    init {
        this.mView = view
    }

    companion object {

        fun init(view: AccountContract.View): AccountPresenter {
            return AccountPresenter(view)
        }
    }

    fun modifyDoctorInfo(modifyType: Int, newContent: String) {

        mView?.showLoading()

        val map = mutableMapOf<String, Any>()

        map[when (modifyType) {
            MODIFY_TYPE_NAME -> {
                "name"
            }
            MODIFY_TYPE_HOSPITAL -> {
                "hospital"
            }
            MODIFY_TYPE_DEPARTMENT -> {
                "department"
            }
            MODIFY_TYPE_JOB_TITLE -> {
                "title"
            }
            else -> {
                "name"
            }
        }] = newContent

        mCall = AppManager.getHttpService().updateDoctorInfo(map)

        mCall?.enqueue(object : BaseSdResponseCallback<DoctorInfo>() {

            override fun onSuccess(response: DoctorInfo?) {
                response?.let {
                    AppManager.getAccountViewModel().updateDoctorInfo(response, true)
                    mView?.onModifySuccess(response)
                }
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.onModifyFailed(error = errorResponse.message)
            }

            override fun onFinish() {
                super.onFinish()
                mView?.dismissLoading()
            }

        })
    }

    fun onRelease() {
        mCall?.cancel()
    }
}