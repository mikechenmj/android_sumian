package com.sumian.sleepdoctor.cbti.bean

/**
 * Created by dq
 *
 * on 2018/7/18
 *
 * desc:  CBTI 视频 帧 log, 用于统计该视频 具体看了多少帧
 */
data class CoursePlayLog(var id: Int, //log id
                         var cbti_id: Int,//购买的cbti id
                         var cbti_course_id: Int,//课程id
                         var video_progress: String,//进度 16进制字符串
                         var end_point: Int,//最后一次离开时间点 秒
                         var finished_at: Int,//观看至70%以上的时间 null:未完成
                         var created_at: Int,//第一次观看时间
                         var updated_at: Int//最后更新时间
) 