package com.sumian.sddoctor.service.cbti.bean

import android.text.TextUtils

data class CBTIWatchLogTask(val cbtiCourseId: Int,
                            val videoId: String,
                            val watchLength: Int) {

    /**
     * 如果这是一个无效的 task 则不执行
     */
    fun isInvaliedTask(): Boolean {
        return cbtiCourseId <= 0 || TextUtils.isEmpty(videoId) || watchLength <= 0
    }
}