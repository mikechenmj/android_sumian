package com.sumian.sleepdoctor.network.body

/**
 *
 *Created by sm
 * on 2018/6/8 10:14
 * desc:
 **/
class AdvisoryRecordBody {

    var include: String? = "user,doctor,records"
    var advisory_id: Int = 0
    var content: String? = null
    var online_report_ids: ArrayList<Int>? = null
    var picture_count: Int = 0

}