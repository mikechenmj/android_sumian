package com.sumian.sddoctor.patient.bean

/**
 * Created by sm
 *
 * on 2018/8/29
 *
 * desc:
 *
 */
data class PatientMeta(val normal: Int,
                       val vip: Int,
                       val super_vip: Int,
                       val consulted: Int,
                       val not_consulted: Int
)