package com.sumian.sddoctor.service.cbti.presenter

import com.sumian.common.mvp.IPresenter.Companion.mCalls
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.App
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.patient.bean.Patient
import com.sumian.sddoctor.service.cbti.bean.CBTIProgressGroup
import com.sumian.sddoctor.service.cbti.bean.CBTIProgressGroupResponse
import com.sumian.sddoctor.service.cbti.contract.CBTIProgressGroupContract

class CBTIProgressGroupPresenter private constructor(view: CBTIProgressGroupContract.View) : CBTIProgressGroupContract.Presenter {

    private var view: CBTIProgressGroupContract.View? = null

    private var currentCBTIProgressGroups: MutableList<CBTIProgressGroup> = mutableListOf()

    init {
        this.view = view
    }

    companion object {
        @JvmStatic
        fun init(view: CBTIProgressGroupContract.View): CBTIProgressGroupContract.Presenter {
            return CBTIProgressGroupPresenter(view)
        }
    }

    override fun initDefaultCBTIProgressGroups() {
        val cbtiGroupArrays = App.getAppContext().resources.getStringArray(R.array.cbti_group_arrays)
        lateinit var cbtiProgressGroup: CBTIProgressGroup
        val patients = mutableListOf<Patient>()
        cbtiGroupArrays.forEach {
            cbtiProgressGroup = CBTIProgressGroup()
            cbtiProgressGroup.title = it
            cbtiProgressGroup.users = patients
            currentCBTIProgressGroups.add(cbtiProgressGroup)
        }
        view?.onGetCBTIProgressGroupsSuccess(currentCBTIProgressGroups)
    }

    override fun getCBTIProgressGroups(groups: String?, isHavePatient: Boolean) {
        view?.showLoading()
        val map = mutableMapOf<String, Any>()

        groups?.let {
            map["groups"] = groups
        }

        if (isHavePatient) {
            map["include"] = "users"
        }

        val call = AppManager.getHttpService().getCBTIProgressGroups(map)
        mCalls.add(call)
        call.enqueue(object : BaseSdResponseCallback<CBTIProgressGroupResponse>() {
            override fun onSuccess(response: CBTIProgressGroupResponse?) {
                response?.let {
                    val data = it.data
                    for (index in data.indices) {
                        data[index].allPatientsCount = it.meta.total_users
                    }
                    if (isHavePatient) {
                        currentCBTIProgressGroups.forEachIndexed { index, cbtiProgressGroup ->
                            if (cbtiProgressGroup.key == groups) {
                                currentCBTIProgressGroups[index].isShow = true
                                currentCBTIProgressGroups[index].users = data[0].users
                            }
                        }
                    } else {
                        currentCBTIProgressGroups = data
                    }
                    view?.onGetCBTIProgressGroupsSuccess(currentCBTIProgressGroups)
                }
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                view?.onGetCBTIProgressGroupsFailed(error = errorResponse.message)
            }

            override fun onFinish() {
                super.onFinish()
                view?.dismissLoading()
            }
        })
    }
}