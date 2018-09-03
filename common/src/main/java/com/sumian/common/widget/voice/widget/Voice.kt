package com.sumian.common.widget.voice.widget

/**
 * Created by dq
 *
 * on 2018/8/29
 *
 * desc:  音频文件
 */
class Voice {

    companion object {

        const val PLAYING_STATUS = 0x01
        const val IDLE_STATUS = 0x00
    }

    var status: Int = IDLE_STATUS  //0x00 未播放状态   //0x01  播放状态
    var duration: Int = 0
    var voicePath: String = ""

}