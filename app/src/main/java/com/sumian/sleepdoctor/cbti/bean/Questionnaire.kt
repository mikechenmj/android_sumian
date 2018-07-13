package com.sumian.sleepdoctor.cbti.bean

/**
 * Created by dq
 *
 * on 2018/7/13
 *
 * desc:   CBTI 每节课的调查问卷/题目
 */
data class Questionnaire(var question: String,//题目/问题
                         var selection: List<String>)// 选项