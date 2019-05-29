package com.sumian.sd.buz.cbti.video.download

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.aliyun.vodplayer.downloader.*
import com.aliyun.vodplayer.media.AliyunPlayAuth
import com.aliyun.vodplayer.media.IAliyunVodPlayer
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import java.io.File
import java.io.FileOutputStream


/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/28 12:18
 * desc   :
 * version: 1.0
 */
object VideoDownloadManager {
    private const val SP_NAME = "VideoDownloadManager"
    private const val CBTI_VIDEO_DIR = "cbti_videos"
    private const val ALIYUN_SECRET_IMAGE_FILE_NAME = "aliyun_secret_image.dat"

    @SuppressLint("StaticFieldLeak")
    private lateinit var downloadManager: AliyunDownloadManager
    private val mDownloadInfoListenerMap = HashMap<String, AliyunDownloadInfoListener>()
    private val mDownloadInfoListener = object : AliyunDownloadInfoListener {
        override fun onPrepared(list: MutableList<AliyunDownloadMediaInfo>) {
            Log.d("Download", "onPrepared")
            val info = list[0]
            downloadManager.addDownloadMedia(info)
            downloadManager.startDownloadMedia(info)
        }

        override fun onCompletion(info: AliyunDownloadMediaInfo) {
            mDownloadInfoListenerMap[info.vid]?.onCompletion(info)
            unregisterDownloadListener(info.vid)
            persistVideoPath(info.vid, info.savePath)
        }

        override fun onProgress(info: AliyunDownloadMediaInfo, progress: Int) {
            mDownloadInfoListenerMap[info.vid]?.onProgress(info, progress)
            LogUtils.d(progress)
        }

        override fun onM3u8IndexUpdate(info: AliyunDownloadMediaInfo?, p1: Int) {
        }

        override fun onWait(info: AliyunDownloadMediaInfo) {
        }

        override fun onError(info: AliyunDownloadMediaInfo, p1: Int, p2: String?, p3: String?) {
        }

        override fun onStart(info: AliyunDownloadMediaInfo) {
        }

        override fun onStop(info: AliyunDownloadMediaInfo) {
        }
    }

    fun init(context: Context) {
        downloadManager = AliyunDownloadManager.getInstance(context)
        downloadManager.addDownloadInfoListener(mDownloadInfoListener)
        val config = AliyunDownloadConfig()
        config.secretImagePath = getEncryptedFile(context).absolutePath
        config.downloadDir = context.getDir(CBTI_VIDEO_DIR, 0).absolutePath
        config.maxNums = 4
        downloadManager.setDownloadConfig(config)
    }

    /**
     * Remember to unregister listener when exit
     * @see#unregisterDownloadListener
     */
    fun download(vid: String, playAuth: String, downloadInfoListener: AliyunDownloadInfoListener? = null) {
        LogUtils.d("$vid, $playAuth")
        val playAuthBuilder = AliyunPlayAuth.AliyunPlayAuthBuilder()
        playAuthBuilder.setVid(vid)
        playAuthBuilder.setPlayAuth(playAuth)
        playAuthBuilder.setQuality(IAliyunVodPlayer.QualityValue.QUALITY_HIGH)
        val auth = playAuthBuilder.build()
        downloadManager.prepareDownloadMedia(auth)
        registerDownloadListener(vid, downloadInfoListener)
        downloadManager.setRefreshAuthCallBack(object : AliyunRefreshPlayAuthCallback {
            override fun refreshPlayAuth(vid: String, quality: String, format: String, title: String, encript: Boolean): AliyunPlayAuth {
                val authBuilder = AliyunPlayAuth.AliyunPlayAuthBuilder()
                authBuilder.setPlayAuth(playAuth)
                authBuilder.setVid(vid)
                authBuilder.title = title
                authBuilder.setQuality(quality)
                authBuilder.setFormat(format)
                authBuilder.isEncripted = if (encript) 1 else 0
                return authBuilder.build()
            }
        })
    }

    fun registerDownloadListener(vid: String?, downloadInfoListener: AliyunDownloadInfoListener?) {
        if (vid == null || downloadInfoListener == null) {
            return
        }
        mDownloadInfoListenerMap[vid] = downloadInfoListener
    }

    fun unregisterDownloadListener(downloadInfoListener: AliyunDownloadInfoListener?) {
        if (downloadInfoListener == null) {
            return
        }
        val iterator = mDownloadInfoListenerMap.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            if (next.value == downloadInfoListener) {
                iterator.remove()
            }
        }
    }

    fun unregisterDownloadListener(vid: String) {
        VideoDownloadManager.unregisterDownloadListener(mDownloadInfoListenerMap[vid])
    }

//    fun getSavedVideoPathByVid(vid: String): String? {
//        val downloadingMedias = downloadManager.downloadingMedias
//        for (downloadMediaInfo in downloadingMedias) {
//            if (downloadMediaInfo.vid == vid) {
//                return if (downloadMediaInfo.progress == 100 && File(downloadMediaInfo.savePath).exists()) {
//                    downloadMediaInfo.savePath
//                } else {
//                    null
//                }
//            }
//        }
//        return getVideoPath()
//    }

    fun getDownloadProgressByVid(vid: String?): Int {
        if (vid == null) {
            return 0
        }
        val downloadingMedias = downloadManager.downloadingMedias
        for (downloadMediaInfo in downloadingMedias) {
            if (downloadMediaInfo.vid == vid) {
                return downloadMediaInfo.progress
            }
        }
        return 0
    }

    private fun copyEncryptedToStorage(context: Context, encryptedFile: File) {
        val assetsManager = context.assets
        val files = assetsManager.list("")
        for (filename in files!!) {
            if (TextUtils.equals(filename, encryptedFile.name)) {
                val inputStream = assetsManager.open(filename)
                val outputStream = FileOutputStream(encryptedFile)
                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
                return
            }
        }
    }

    private fun getEncryptedFile(context: Context): File {
        val encryptFile = File(context.getDir(CBTI_VIDEO_DIR, 0), ALIYUN_SECRET_IMAGE_FILE_NAME)
        if (!encryptFile.exists()) {
            copyEncryptedToStorage(context, encryptFile)
        }
        return encryptFile
    }

    private fun getSp(): SPUtils {
        return SPUtils.getInstance(SP_NAME)
    }

    private fun persistVideoPath(vid: String, videoPath: String) {
        getSp().put(vid, videoPath)
    }

    fun getSavedVideoPathByVid(vid: String?): String? {
        if (vid == null) {
            return null
        }
        val path = getSp().getString(vid)
        return if (File(path).exists()) {
            path
        } else {
            null
        }
    }

    abstract class AliyunDownloadInfoListenerEmptyImpl : AliyunDownloadInfoListener {
        override fun onPrepared(p0: MutableList<AliyunDownloadMediaInfo>?) {
        }

        override fun onM3u8IndexUpdate(p0: AliyunDownloadMediaInfo?, p1: Int) {
        }

        override fun onWait(p0: AliyunDownloadMediaInfo?) {
        }

        override fun onError(p0: AliyunDownloadMediaInfo?, p1: Int, p2: String?, p3: String?) {
        }

        override fun onStart(p0: AliyunDownloadMediaInfo?) {
        }

        override fun onStop(p0: AliyunDownloadMediaInfo?) {
        }
    }

}