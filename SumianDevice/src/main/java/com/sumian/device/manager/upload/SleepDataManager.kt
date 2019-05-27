package com.sumian.device.manager.upload

import android.annotation.SuppressLint
import android.content.Context
import com.blankj.utilcode.util.SPUtils
import com.google.gson.reflect.TypeToken
import com.sumian.device.manager.upload.UploadFileCallback.Companion.ERROR_CODE_DUPLICATE_UPLOAD
import com.sumian.device.manager.upload.bean.UploadSleepDataParams
import com.sumian.device.manager.upload.bean.UploadSleepDataTask
import com.sumian.device.util.JsonUtil
import com.sumian.device.util.LogManager
import java.io.File

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/6 18:15
 * desc   :
 * version: 1.0
 */
@SuppressLint("StaticFieldLeak")
object SleepDataManager {
    private const val SP_KEY_TASKS = "UploadSleepFileTasks"
    private const val MAX_RETRY_COUNT = 3
    private lateinit var mContext: Context
    private var mIsUploading = false
    private var mRetryCount = 0

    fun init(context: Context) {
        mContext = context.applicationContext
    }

    fun saveAndUploadData(
            context: Context,
            sleepData: ArrayList<String?>,
            transId: String,
            transType: Int,
            monitorSn: String?,
            sleepMasterSn: String?,
            receiveStartTime: Int,
            receiveEndTime: Int
    ) {
        val file = saveDataToFile(context, transType, transId, monitorSn, sleepData)
        val uploadSleepDataParams = UploadSleepDataParams(
                file.name,
                monitorSn,
                sleepMasterSn,
                transType,
                receiveStartTime,
                receiveEndTime
        )
        val task = UploadSleepDataTask(
                file.absolutePath,
                uploadSleepDataParams
        )
        addTask(task)
        uploadFile(task)
    }

    fun uploadNextTask() {
        if (mIsUploading) {
            return
        }
        val nextTask = getNextTask()
        if (nextTask != null) {
            uploadFile(nextTask)
        }
    }

    private fun uploadFile(task: UploadSleepDataTask) {
        mIsUploading = true
        val filePath = task.filePath
        LogManager.log("upload sleep data $filePath")
        SleepFileUploadUtil.uploadSleepFile(mContext, task, object : UploadFileCallback {
            override fun onSuccess(result: String?) {
                LogManager.log("upload sleep data $filePath success")
                mIsUploading = false
                mRetryCount = 0
                removeTask(task)
                uploadNextTask()
            }

            override fun onFail(code: Int, msg: String?) {
                mIsUploading = false
                LogManager.log("upload sleep data $filePath fail: $msg $mRetryCount")
                if (code == ERROR_CODE_DUPLICATE_UPLOAD) {
                    mRetryCount = 0
                    removeTask(task)
                    uploadNextTask()
                } else if (mRetryCount < MAX_RETRY_COUNT) {
                    mRetryCount++
                    uploadNextTask()
                }
            }
        })
    }

    private fun addTask(task: UploadSleepDataTask) {
        val allTasks = getAllTasks()
        if (allTasks.contains(task)) {
            return
        }
        allTasks.add(task)
        setAllTasks(allTasks)
    }

    private fun removeTask(task: UploadSleepDataTask) {
        val allTasks = getAllTasks()
        allTasks.remove(task)
        setAllTasks(allTasks)
    }

    private fun getAllTasks(): MutableList<UploadSleepDataTask> {
        val json = getSp().getString(SP_KEY_TASKS)
        var list = JsonUtil.fromJson<MutableList<UploadSleepDataTask>>(
                json,
                object : TypeToken<MutableList<UploadSleepDataTask>>() {}.type
        )
        if (list == null) {
            list = ArrayList()
        }
        return list
    }

    private fun getNextTask(): UploadSleepDataTask? {
        val allTasks = getAllTasks()
        return if (allTasks.isEmpty()) {
            null
        } else {
            allTasks[0]
        }
    }

    private fun setAllTasks(tasks: MutableList<UploadSleepDataTask>) {
        getSp().put(SP_KEY_TASKS, JsonUtil.toJson(tasks))
    }

    private fun getSp(): SPUtils {
        return SPUtils.getInstance(SleepDataManager.javaClass.simpleName)
    }

    private fun saveDataToFile(
            context: Context,
            transType: Int,
            transId: String,
            monitorSn: String?,
            sleepData: ArrayList<String?>
    ): File {
        val dir = context.getDir("SleepData", 0)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val fileName = createFileName(transType, transId, monitorSn ?: "sn")
        val file = File(dir, fileName)
        file.printWriter().use {
            for (cmd in sleepData) {
                it.println(cmd)
            }
        }
        return file
    }

    private fun createFileName(type: Int, transId: String, sn: String): String {
        return "${type}_${transId}_$sn.txt"
    }


    private fun log(log: String) {

    }
}