package com.sumian.sd.common.h5.music

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import android.widget.Toast
import com.sumian.common.utils.SumianExecutor.runOnUiThread
import com.sumian.sd.ICallbackAidl
import com.sumian.sd.IMusicServiceAidl
import com.sumian.sd.common.h5.music.bean.MusicInfo
import java.lang.Exception

class H5MusicHelper(private val context: Context) {

    private var mIMusicServiceAidl: IMusicServiceAidl? = null
    var isBound = false
    private var mOnBindStateChangeListener: OnBindStateChangeListener? = null
    private var mOnPlayStatusChangeListener: OnPlayStatusChangeListener? = null

    fun setOnBindStateChangeListener(onBindStateChangeListener: OnBindStateChangeListener?) {
        mOnBindStateChangeListener = onBindStateChangeListener
    }

    fun setOnPlayStatusChangeListener(onPlayStatusChangeListener: OnPlayStatusChangeListener?) {
        Log.i("MCJ", "setOnPlayStatusChangeListener")
        mOnPlayStatusChangeListener = onPlayStatusChangeListener
    }

    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mIMusicServiceAidl = IMusicServiceAidl.Stub.asInterface(service)
            if (!service.isBinderAlive) {
                Toast.makeText(context, "连接音乐后台服务失败", Toast.LENGTH_SHORT).show()
                return
            }
            isBound = true
            mOnBindStateChangeListener?.onChange(true)
            try {
                mIMusicServiceAidl?.setCallback(object : ICallbackAidl.Stub() {
                    override fun onComplete() {
//                        runOnUiThread(Runnable { })
                    }

                    override fun onPlayStatusChange(status: Int) {
                        Log.i("MCJ", "onPlayStatusChange: $status")
                        mOnPlayStatusChangeListener?.onStatusChange(status)
                    }
                })
                mIMusicServiceAidl?.startMusic()
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            onUnbind()
        }
    }

    fun bindService(path: String) {
        val intent = Intent(context, MusicService::class.java)
        intent.putExtra(MusicService.EXTRA_PATH, path)
        context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
    }

    fun onUnbind() {
        mIMusicServiceAidl = null
        mOnBindStateChangeListener?.onChange(false)
        isBound = false
    }

    fun startMusic() {
        mIMusicServiceAidl?.startMusic()
    }

    fun seekMusic(msec: Int, status: Int) {
        mIMusicServiceAidl?.seekMusic(msec, status)
    }

    fun switchMusic(path: String) {
        mIMusicServiceAidl?.switchMusic(path)
    }

    fun pauseMusic() {
        mIMusicServiceAidl?.pauseMusic()
    }

    fun stopMusic() {
        if (isBound) {
            mIMusicServiceAidl?.stopMusic()
            context.unbindService(mConnection)
            onUnbind()
        }
    }

    fun getMusicInfo(): MusicInfo? {
        return mIMusicServiceAidl?.musicInfo
    }

    private fun notifyStatusChange() {

    }

    interface OnBindStateChangeListener {
        fun onChange(isBound: Boolean)
    }

    interface OnPlayStatusChangeListener {
        fun onStatusChange(status: Int)
    }
}
