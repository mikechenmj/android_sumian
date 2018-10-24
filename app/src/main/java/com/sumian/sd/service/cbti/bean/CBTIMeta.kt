package com.sumian.sd.service.cbti.bean

/**
 * Created by dq
 *
 * on 2018/7/19
 *
 * desc:CBTI  额外附带信息
 */
data class CBTIMeta(val chapter_progress: Int,
                    val last_chapter_summary: String?,
                    val last_chapter_summary_rtf: String?,
                    val chapter: Chapter) {

    data class Chapter(var id: Int,
                       var title: String,
                       var banner: String,
                       var introduction: String,
                       var summary: String,
                       var summary_rtf: String,
                       var index: Int)
}