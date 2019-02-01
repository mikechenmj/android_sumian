package com.sumian.sddoctor.patient.bean

/**
 * Created by sm
 *
 * on 2018/8/29
 *
 * desc:  分组患者Response
 *
 */
data class GroupPatientResponse(val data: ArrayList<ArrayList<Patient>>,
                                val meta: PatientMeta) {

    fun isEmpty(): Boolean {
        return meta.vip + meta.super_vip + meta.normal + meta.consulted + meta.not_consulted <= 0
    }
}