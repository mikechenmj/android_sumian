package com.sumian.sd.common.h5.music

import android.content.Context
import android.util.Log
import com.github.lzyzsd.jsbridge.CallBackFunction
import com.google.gson.reflect.TypeToken
import com.sumian.common.h5.widget.SWebView
import com.sumian.common.helper.ToastHelper
import com.sumian.common.utils.JsonUtil
import com.sumian.sd.common.h5.music.bean.MusicInfo

class H5MusicCommunication(context: Context) {

    private val mH5MusicHelper = H5MusicHelper(context)
    private var mList: Array<String> = emptyArray()
    private var mIndex = -1

    private var mPlayEndCallback: CallBackFunction? = null

    fun registerHandler(sWebView: SWebView) {
        mH5MusicHelper.setOnPlayStatusChangeListener(object : H5MusicHelper.OnPlayStatusChangeListener {
            override fun onStatusChange(status: Int) {
                Log.i("MCJ","onStatusChange: $status $mPlayEndCallback")
                if (status == MusicInfo.STATUS_STOP) {
                    mPlayEndCallback?.onCallBack(getMusicInfoJson())
                }
            }
        })
        sWebView.registerHandler("itemPlayEnd") { data, function ->
            Log.i("MCJ","registerHandler itemPlayEnd")
            mPlayEndCallback = function
        }
        sWebView.registerHandler("playAudio") { data, function ->
            val type = object : TypeToken<H5PlayAudioData>() {}
            val h5PlayAudioData = JsonUtil.fromJson<H5PlayAudioData>(data, type.type)
            Log.i("MCJ", "registerHandler playAudio: $h5PlayAudioData")
            if (h5PlayAudioData == null) {
                ToastHelper.show("未获取到播放资源，播放失败")
                return@registerHandler
            }
            val currentPath = h5PlayAudioData.urls[h5PlayAudioData.index]
            mList = h5PlayAudioData.urls
            mIndex = h5PlayAudioData.index
            if (!mH5MusicHelper.isBound) {
                mH5MusicHelper.bindService(currentPath)
                mH5MusicHelper.setOnBindStateChangeListener(object : H5MusicHelper.OnBindStateChangeListener {
                    override fun onChange(isBound: Boolean) {
                        if (isBound) {
                            function.onCallBack(getMusicInfoJson())
                            mH5MusicHelper.setOnBindStateChangeListener(null)
                        }
                    }
                })
            } else {
                mH5MusicHelper.switchMusic(currentPath)
                function.onCallBack(getMusicInfoJson())
            }
        }
        sWebView.registerHandler("resumeAudio") { data, function ->
            Log.i("MCJ", "registerHandler resumeAudio")
            mH5MusicHelper.startMusic()
            function.onCallBack(getMusicInfoJson())
        }
        sWebView.registerHandler("pauseAudio") { data, function ->
            Log.i("MCJ", "registerHandler pauseAudio")
            mH5MusicHelper.pauseMusic()
            function.onCallBack(getMusicInfoJson())
        }
        sWebView.registerHandler("stopAudio") { data, function ->
            Log.i("MCJ", "registerHandler stopAudio")
            mH5MusicHelper.stopMusic()
            function.onCallBack(getMusicInfoJson())
        }
        sWebView.registerHandler("seekAudio") { data, function ->
            Log.i("MCJ", "registerHandler seekAudio")
            val type = object : TypeToken<SeekData>() {}
            val seekData = JsonUtil.fromJson<SeekData>(data, type.type)
            if (seekData == null) {
                ToastHelper.show("读取进度失败")
                return@registerHandler
            }
            mH5MusicHelper.seekMusic(seekData.time, seekData.status)
            function.onCallBack(getMusicInfoJson())
        }
        sWebView.registerHandler("getAudioInfo") { data, function ->
            Log.i("MCJ", "registerHandler getAudioInfo")
            function.onCallBack(getMusicInfoJson())
        }
    }

    private fun getMusicInfoJson(): String {
        val data = mH5MusicHelper.getMusicInfo()
                ?: return JsonUtil.toJson(H5MusicInfo(H5MusicInfo.Result(error = "未能获取到音频信息")))
        val h5MusicInfo = H5MusicInfo(H5MusicInfo.Result(
                data.path, data.status == MusicInfo.STATUS_STOP, data.status == MusicInfo.STATUS_PAUSE || data.status == MusicInfo.STATUS_STOP,
                data.duration.toFloat() / 1000, data.currentPosition.toFloat() / 1000, index = mIndex
        ))
        Log.i("MCJ", "h5MusicInfo $h5MusicInfo")
        return JsonUtil.toJson(h5MusicInfo)
    }
}

data class H5MusicInfo(val result: Result = Result()) {
    data class Result(
            val src: String = "", val stoped: Boolean = false, val paused: Boolean = false,
            val duration: Float = 0.toFloat(), val currentTime: Float = 0.toFloat(),
            val playedTime: Float = (-1).toFloat(), val maxPlayTime: Float = (-1).toFloat(),
            val index: Int = 0, val error: String = "")
}

data class H5PlayAudioData(val urls: Array<String> = emptyArray(), val index: Int = 0) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as H5PlayAudioData

        if (!urls.contentEquals(other.urls)) return false

        return true
    }

    override fun hashCode(): Int {
        return urls.contentHashCode()
    }
}

data class SeekData(val time: Int = -1, val status: Int = 0)