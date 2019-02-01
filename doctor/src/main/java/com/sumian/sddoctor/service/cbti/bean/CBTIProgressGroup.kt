package com.sumian.sddoctor.service.cbti.bean

import com.sumian.sddoctor.patient.bean.Patient
import java.util.*

/**
 * Created by sm
 *
 * on 2018/8/29
 *
 * desc:  CBTI  患者进度分组 标签小组
 *
 */
class CBTIProgressGroup {

    var isShow = false
    var title: String = ""
    var count: Int = 0
    var key: String = ""  //分别对应的当前的标签 1,2,3,4,5,6,passed,failed [1-6周,已达标,未达标]
    var users: MutableList<Patient>? = null
    var allPatientsCount: Int = 0

    fun formatTagTips(): String {
        return String.format(Locale.getDefault(), "%s（%d人）", title, count)
    }

}