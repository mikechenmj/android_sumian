package com.sumian.sleepdoctor.improve.advisory.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *
 *Created by sm
 * on 2018/6/4 14:56
 * desc:提问或者回复记录
 **/
@Parcelize
data class Record(var id: Int,//记录 ID
                  var advisory_id: Int,//咨询 id
                  var type: Int,//0：提问，1：回复
                  var content: String,//记录的内容
                  var created_at: Int,//记录时间  unix 时间戳
                  var update_at: Int,//记录更新时间  Unix 时间戳
                  var question_index: Int,//第几次提问, type为0才返回
                  var images: ArrayList<String>,//图片 URL 列表
                  var reports: ArrayList<Report>//在线报告列表
) : Parcelable {

    companion object {
        const val RECORD_QUESTION_TYPE = 0x00
        const val RECORD_REPLY_TYPE = 0x01
    }
}
