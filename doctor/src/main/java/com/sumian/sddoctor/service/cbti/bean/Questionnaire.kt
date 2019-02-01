package com.sumian.sddoctor.service.cbti.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 * Created by dq
 *
 * on 2018/7/13
 *
 * desc:   CBTI 每节课的调查问卷/题目
 */
@Parcelize
data class Questionnaire(var question: String,//题目/问题
                         var explanations: List<String>,//说明
                         var answer: Boolean,//是否回答
                         var selection: List<String>)// 选项
    : Serializable, Parcelable