package com.sumian.sddoctor.patient.bean

import java.util.*

/**
 * Created by sm
 *
 * on 2018/8/29
 *
 * desc:  分组 patient
 *
 */
class Group {

    var isShow = false
    var tagTip: String = ""
    var patientSize: Int = 0
    var type: Int = 0  //分别对应的当前的标签
    var patients: MutableList<Patient>? = null
    var allPatientsCount: Int = 0


    fun formatTagTips(): String {
        return String.format(Locale.getDefault(), "%s（%d人）", tagTip, patientSize)
    }

}