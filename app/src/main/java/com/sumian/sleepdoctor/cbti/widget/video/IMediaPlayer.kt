package com.sumian.sleepdoctor.cbti.widget.video

/**
 * Created by dq
 *
 * on 2018/7/18
 *
 * desc:
 */
interface IMediaPlayer {

    fun play()

    fun play(position: Int)

    fun reset()

    fun release()

    fun rePlay()
}