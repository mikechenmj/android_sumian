package com.sumian.sleepdoctor.cbti.bean

/**
 * Created by dq
 *
 * on 2018/7/16
 *
 * desc: CBTI 视频播放鉴权 bean,即获取到的视频播放权限,及该课时下的其他相关信息
 */
data class CoursePlayAuth(var id: Int,
                          var title: String,
                          var banner: String,
                          var introduction: String,
                          var summary: String?,
                          var index: Int,
                          var last_chapter_summary: String,
                          var courses: List<Course>,
                          var meta: Meta) {

    data class Meta(var play_auth: String,//播放凭证 填入aliyun播放器
                    var video_id: String,//video-id
                    var course_log: Log,//视频播放记录 如果是首次播放 null
                    var is_pop_questionnaire: Boolean,
                    var questionnaire: List<Questionnaire>,
                    var exercise: Exercise,
                    var exercise_is_filled: Boolean,
                    var chapter_progress: Int)

    data class Log(var id: Int,//播放记录id
                   var cbti_course_id: Int,//视频id
                   var video_progress: String,//16进制 播放进度
                   var end_point: Int,//上次播放到的秒
                   var finished_at: Int,//播放完成（70%）的时间 未完成 null
                   var created_at: Int)//首次观看时间
}