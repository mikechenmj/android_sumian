package com.sumian.devicedemo.dfuDemo

import android.content.Context
import android.util.Log
import com.blankj.utilcode.util.ToastUtils
import com.leo618.zip.IZipCallback
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.sumian.devicedemo.dfuDemo.updater.DataPackageUpdater
import com.sumian.devicedemo.dfuDemo.updater.InitPackageUpdater
import com.sumian.devicedemo.dfuDemo.util.UnZipUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import java.io.File

class DfuUpdateHelper(val context: Context, private val deviceId: String) {

    fun start() {
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            val files = unZip(coroutineScope, downloadUpdateZip(coroutineScope, DFU_FILE_URL))
            val initPackage = readFile(files[0])
            val imagePackage = readFile(files[1])
            Log.i("MCJ", "init 包大小: ${initPackage.size} image 包大小: ${imagePackage.size}")
            if (!updateInit(initPackage).log("传输 init 包结果: ") || !(updateImage(imagePackage).log("传输 image 包结果: "))) {
                Log.i("MCJ", "传输失败，重试")
                retryUpdate(initPackage, imagePackage).log("重试结果: ")
            }
        }
    }

    private suspend fun downloadUpdateZip(coroutineScope: CoroutineScope, url: String): File {
        val channel = Channel<File>(CONFLATED)
        val dir = context.getDir("upgrade", 0)
        val file = File(dir, "sumian_dfu_upgrade.zip")
        FileDownloader.setup(context)
        FileDownloader.getImpl().create(url)
                .setPath(file.path)
                .setListener(object : FileDownloadListener() {
                    override fun warn(task: BaseDownloadTask?) {
                    }

                    override fun completed(task: BaseDownloadTask?) {
                        Log.i("MCJ", "下载文件成功")
                        coroutineScope.launch {
                            channel.send(file)
                            channel.close()
                        }

                    }

                    override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }

                    override fun error(task: BaseDownloadTask?, e: Throwable?) {
                        ToastUtils.showShort(e?.message)
                    }

                    override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }

                    override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }
                })
                .start()
        return channel.receive()
    }

    private suspend fun unZip(coroutineScope: CoroutineScope, file: File): Array<File> {
        val channel = Channel<Array<File>>(CONFLATED)
        val unzipDir = File(context.cacheDir.absolutePath, "sumian")
        UnZipUtil.unzip(file.absolutePath, unzipDir.absolutePath, object : IZipCallback {
            override fun onStart() {
            }

            override fun onProgress(percentDone: Int) {
            }

            override fun onFinish(success: Boolean) {
                Log.i("MCJ", "解压文件成功")
                val array = arrayOf(File(unzipDir.absolutePath, "Monitor.dat"), File(unzipDir.absolutePath, "Monitor.bin"))
                coroutineScope.launch {
                    channel.send(array)
                    channel.close()
                }
            }
        })
        return channel.receive()
    }

    private suspend fun updateInit(file: ByteArray, force: Boolean = false): Boolean {
        return InitPackageUpdater(file, deviceId).startUpdate(force)
    }

    private suspend fun updateImage(file: ByteArray, force: Boolean = false): Boolean {
        return DataPackageUpdater(file, deviceId).startUpdate(force)
    }

    private suspend fun retryUpdate(initPackage: ByteArray, imagePackage: ByteArray): Boolean {
        return updateInit(initPackage, true) && updateImage(imagePackage, true)
    }

    private suspend fun readFile(file: File) = withContext(Dispatchers.Default) {
        file.readBytes()
    }
}