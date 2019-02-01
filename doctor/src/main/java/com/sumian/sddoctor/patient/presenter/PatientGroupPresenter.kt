package com.sumian.sddoctor.patient.presenter

import android.net.Uri
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.patient.bean.Group
import com.sumian.sddoctor.patient.bean.GroupPatientResponse
import com.sumian.sddoctor.patient.bean.Patient
import com.sumian.sddoctor.patient.contract.PatientGroupContract

class PatientGroupPresenter private constructor(view: PatientGroupContract.View) : PatientGroupContract.Presenter {


    private var mGroups: MutableList<Group>? = null

    override fun setGroups(groups: MutableList<Group>) {
        this.mGroups = groups
    }

    private var mView: PatientGroupContract.View? = null

    private var mIsRefresh = false

    private var mIsLevel = true

    private var mLevelOrFace = intArrayOf(0, 1, 2)

    init {
        this.mView = view
    }

    companion object {

        fun init(view: PatientGroupContract.View): PatientGroupContract.Presenter {
            return PatientGroupPresenter(view)
        }
    }

    override fun getLevelGroupPatients(vararg patientLevels: Int) {
        this.mLevelOrFace = patientLevels
        getPatients(true, patientLevels)
    }

    override fun getFaceGroupPatients(vararg patientFaces: Int) {
        this.mLevelOrFace = patientFaces
        getPatients(false, patientFaces)
    }

    override fun refreshGroupPatients() {
        mIsRefresh = true
        getPatients(mIsLevel, mLevelOrFace)
    }

    private fun getPatients(isLevel: Boolean, levelOrFace: IntArray) {
        this.mIsLevel = isLevel
        this.mLevelOrFace = levelOrFace

        mView?.showLoading()

        val sb = StringBuilder("doctor/group-users?")

        levelOrFace.forEachIndexed { index, i ->
            if (index == 0) {
                if (isLevel) {
                    sb.append("tag[]=$i")
                } else {
                    sb.append("consulted[]=$i")
                }
            } else {
                if (isLevel) {
                    sb.append("&tag[]=$i")
                } else {
                    sb.append("&consulted[]=$i")
                }
            }
        }

        val encodeUri = Uri.decode(sb.toString())

        val call = AppManager.getHttpService().getGroupPatients(encodeUri)

        call.enqueue(object : BaseSdResponseCallback<GroupPatientResponse>() {

            override fun onSuccess(response: GroupPatientResponse?) {
                response?.let {
                    mIsRefresh = false

                    it.data.forEachIndexed { index, patients ->
                        run {
                            if (isLevel) {
                                levelGroups[index].patients = patients
                                levelGroups[index].patientSize = patients.size
                            } else {
                                faceGroups[index].patients = patients
                                faceGroups[index].patientSize = patients.size
                            }
                        }
                    }

                    mGroups = if (isLevel) {
                        val patientCount = it.meta.normal + it.meta.super_vip + it.meta.vip
                        levelGroups[2].allPatientsCount = patientCount
                        levelGroups
                    } else {
                        val patientCount = it.meta.consulted + it.meta.not_consulted
                        faceGroups[1].allPatientsCount = patientCount
                        faceGroups
                    }

                    if (mIsRefresh) {
                        mView?.onRefreshGroupsSuccess(mGroups!!)
                    } else {
                        mView?.onGetGroupsSuccess(mGroups!!)
                    }

                    if (it.isEmpty()) {
                        mView?.onShowEmptyView()
                    } else {
                        mView?.onHideEmptyView()
                    }
                }
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.onGetGroupsFailed(errorResponse.message)
            }

            override fun onFinish() {
                mView?.dismissLoading()
            }

        })
    }

    private val faceGroups: MutableList<Group>  by lazy {
        val faceGroups = mutableListOf<Group>()

        val facedGroup = Group()
        facedGroup.tagTip = "已面诊"
        facedGroup.patientSize = 0
        facedGroup.type = Patient.FACED_TYPE

        faceGroups.add(facedGroup)

        val unFacedGroup = Group()
        unFacedGroup.tagTip = "未面诊"
        unFacedGroup.patientSize = 0
        unFacedGroup.type = Patient.UN_FACED_TYPE

        faceGroups.add(unFacedGroup)

        return@lazy faceGroups
    }

    private val levelGroups: MutableList<Group>  by lazy {

        val levelGroups = mutableListOf<Group>()

        val svipGroup = Group()
        svipGroup.tagTip = "SVIP"
        svipGroup.patientSize = 0
        svipGroup.type = Patient.SVIP_LEVEL

        levelGroups.add(svipGroup)

        val vipGroup = Group()
        vipGroup.tagTip = "VIP"
        vipGroup.patientSize = 0
        vipGroup.type = Patient.VIP_LEVEL

        levelGroups.add(vipGroup)

        val normalGroup = Group()
        normalGroup.tagTip = " 普通用户"
        normalGroup.patientSize = 0
        normalGroup.type = Patient.NORMAL_LEVEL

        levelGroups.add(normalGroup)

        return@lazy levelGroups
    }

}