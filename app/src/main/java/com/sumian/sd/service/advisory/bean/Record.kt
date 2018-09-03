package com.sumian.sd.service.advisory.bean

import android.os.Parcelable
import com.sumian.common.widget.voice.widget.Voice
import com.sumian.sd.onlinereport.OnlineReport
import kotlinx.android.parcel.Parcelize

/**
 *
 *Created by sm
 * on 2018/6/4 14:56
 * desc:提问或者回复记录
 **/
@Parcelize
data class Record(var id: Int,//记录 ID
                  var traceable_id: Int,
                  var traceable_type: String,
                  var advisory_mission_id: Int,//咨询 id
                  var type: Int, //类型  0：提问 1：回答
                  var content_type: Int,//内容类型 0：文字 1：声音
                  var sound: Sound, //content_type=1时取此值 如果没有，为null
                  var content: String,//记录的内容  content_type=0时取此值
                  var created_at: Int,//记录时间  unix 时间戳
                  var updated_at: Int,//记录更新时间  Unix 时间戳
                  var question_index: Int,//第几次提问, type为0才返回
                  var images: ArrayList<String>,//图片 URL 列表
                  var reports: ArrayList<OnlineReport>//在线报告列表
) : Parcelable {

    companion object {
        const val RECORD_QUESTION_TYPE = 0x00
        const val RECORD_REPLY_TYPE = 0x01
    }

    @Parcelize
    data class Sound(var url: String, //声音url
                     var duration: Int//声音时长 单位：秒
    ) : Parcelable {

        fun getFormatVoice(): Voice {
            val voice = Voice()
            voice.duration = duration
            voice.voicePath = url
            voice.status = Voice.IDLE_STATUS
            return voice
        }

    }
}