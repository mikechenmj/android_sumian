package com.sumian.sddoctor.service.cbti

import com.sumian.sddoctor.service.cbti.bean.CBTIProgressGroup

/**
 * Created by dq
 *
 * on 2018/8/30
 *
 * desc:
 */
interface ITrigger {

    fun onTrigger(position: Int, group: CBTIProgressGroup)
}