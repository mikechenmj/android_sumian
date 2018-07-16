package com.sumian.sleepdoctor.cbti.bean

/**
 * Created by dq
 *
 * on 2018/7/13
 *
 * desc:  CBTI  练习
 */
data class Exercise(var id: Int,
                    var cbti_course_id: Int,
                    var title: String,
                    var guide: Any,
                    var type: Int,//练习类型 0：判断题 1：勾选题 2：量表题 3：跳转题
                    var data: Any,
                    var final_words: String,
                    var is_lock: Boolean,
                    var done: Boolean) {

    data class Meta(var chapter_progress: Int)
}