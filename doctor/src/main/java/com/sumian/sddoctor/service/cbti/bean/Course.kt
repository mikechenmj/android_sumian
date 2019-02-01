package com.sumian.sddoctor.service.cbti.bean

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by dq
 *
 * on 2018/7/12
 *
 * desc:   获取到的课程
 */
@Parcelize
data class Course(var id: Int,//课程id
                  var cbti_chapter_id: Int,//所属章节id
                  var title: String,
                  var video_id: String,//aliyun video id
                  var duration: Int,
                  var index: Int,
                  var summary: String,
                  var summary_rtf: String,//课程总结，富文本格式
                  @SerializedName("regard_as_done") val regardAsDone: Boolean,//是否完成70%
                  var current_course: Boolean) : Parcelable {

}