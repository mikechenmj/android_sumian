package com.sumian.sddoctor.patient.adapter

import com.sumian.sddoctor.patient.bean.Group

/**
 * Created by dq
 *
 * on 2018/8/30
 *
 * desc:
 */
interface ITrigger {

    fun onTrigger(position: Int, group: Group)
}