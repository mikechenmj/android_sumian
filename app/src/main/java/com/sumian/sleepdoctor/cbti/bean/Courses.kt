package com.sumian.sleepdoctor.cbti.bean

/**
 * Created by dq
 *
 * on 2018/7/13
 *
 * desc:CBTI 章节
 */
data class Courses(var data: List<Lesson>,
                   var meta: Meta) {

    data class Meta(var chapter_progress: Int)
}
