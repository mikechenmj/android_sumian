package com.sumian.sd.buz.cbti.bean

/**
 * Created by dq
 *
 * on 2018/7/19
 *
 * desc:CBTI  列表信息,包括 courses/exercises
 */
data class CBTIDataResponse<Data>(var data: List<Data>, var meta: CBTIMeta) {

}