package com.sumian.sleepdoctor.cbti.bean

/**
 * Created by dq
 *
 * on 2018/7/12
 *
 * desc:
 */
data class Lesson(var id: Int,
                  var cbti_chapter_id: Int,
                  var title: String,
                  var duration: Int,
                  var index: Int,
                  var questionnaire: List<Questionnaire>?,//每节课课后.调查问卷/题目
                  var is_lock: Boolean,//锁定状态 true：锁定 false：开启
                  var regard_as_done: Boolean)//是否看做完成 true：是（复习） false：不是（学习）