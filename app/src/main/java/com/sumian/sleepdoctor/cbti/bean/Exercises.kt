package com.sumian.sleepdoctor.cbti.bean

/**
 * Created by dq
 *
 * on 2018/7/13
 *
 * desc:  CBTI  练习组
 */
data class Exercises(var data: List<Exercise>,
                     var meta: Meta) {

    data class Meta(var chapter_progress: Int)
}