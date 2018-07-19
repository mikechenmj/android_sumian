package com.sumian.sleepdoctor.cbti.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

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
                  var questionnaire: List<Questionnaire>,//每节课课后.调查问卷/题目
                  var is_lock: Boolean,//锁定状态 true：锁定 false：开启
                  var regard_as_done: Boolean,//是否看做完成 true：是（复习） false：不是（学习）
                  var current_course: Boolean) : Parcelable {

}