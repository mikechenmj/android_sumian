package com.sumian.device.manager.helper

import com.blankj.utilcode.util.LogUtils
import com.sumian.device.callback.AsyncCallback
import com.sumian.device.callback.BleRequestCallback
import com.sumian.device.cmd.BleCmd
import com.sumian.device.data.PatternData
import com.sumian.device.manager.DeviceManager
import com.sumian.device.net.NetworkManager
import com.sumian.device.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/6 18:15
 * desc   :
 * 该类用于从服务器下拉pattern数据同步到设备
 *
 * query pattern
 * w: aa4c
 * r: 55 4c ee 03   // 03 表示有3条数据
 * r: 55 4c 05 10018f2050
 * r: 55 4c 05 2002bf2050
 * r: 55 4c 05 3003bf20a0
 *
 * write pattern
 * w: aa 4a 05 10018f2050
 * r: 55 4a 03 100188
 * w: aa 4a 05 2002bf2050
 * r: 55 4a 03 200288
 * w: aa 4a 05 3003bf20a0
 * r: 55 4a 03 300388
 *
 * version: 1.0
 */
object SyncPatternHelper {

    private const val MAX_RETRY_COUNT_OF_SENDING_PATTERN = 3

    fun syncPattern() {
        queryPatternFromServer()
    }

    private fun queryPatternFromServer() {
        val call = NetworkManager.getApi().getUserPattern()
        call.enqueue(object : Callback<List<PatternData>> {
            override fun onFailure(call: Call<List<PatternData>>, t: Throwable) {
                LogUtils.d(t.message)
            }

            override fun onResponse(
                    call: Call<List<PatternData>>,
                    response: Response<List<PatternData>>
            ) {
                LogUtils.d(response)
                if (response.isSuccessful) {
                    val list = response.body()
                    list?.apply {
                        val patternList = ArrayList<ByteArray>()
                        for (data in list) {
                            val value = data.value
                            val patternBytes =
                                    BleCmdUtil.createDataFromString(BleCmd.SET_PATTERN, value)
                            patternList.add(patternBytes)
                        }
                        sendPatternsToDevice(patternList)
                    }
                }
            }
        })
    }

    private fun sendPatternsToDevice(data: ArrayList<ByteArray>) {
        if (data.size == 0) {
            return
        }
        sendPatternsToDevice(data, 0, 0, object : AsyncCallback<Any> {
            override fun onSuccess(data: Any?) {
                LogManager.log("sync pattern success")
            }

            override fun onFail(code: Int, msg: String) {
                LogManager.log("sync pattern fail: $msg")
            }
        })
    }

    /**
     * 迭代发送所有 pattern 到 device
     * @param pattens   所有pattern
     * @param position  单签pattern position
     * @param retryCount 当前position重试次数
     * @param callback  回调
     */
    private fun sendPatternsToDevice(
            pattens: ArrayList<ByteArray>,
            position: Int,
            retryCount: Int,
            callback: AsyncCallback<Any>
    ) {
        if (pattens.size == 0) {
            return
        }
        sendPatternToDevice(pattens[position], object : AsyncCallback<Any> {
            override fun onSuccess(data: Any?) {
                if (position >= pattens.size - 1) {
                    callback.onSuccess()
                } else {
                    // 因为 BleRequestCallback 是以 cmd 为 key 存起来的，
                    // 在一个 request 的 response 里做相同 cmd 的 request ，BleRequestCallback 会被立马移除，
                    // 所以加上delay 避免第二次得不到响应。
                    ThreadManager.postToUIThread({
                        sendPatternsToDevice(pattens, position + 1, 0, callback)
                    }, 10)
                }
            }

            override fun onFail(code: Int, msg: String) {
                if (retryCount < MAX_RETRY_COUNT_OF_SENDING_PATTERN) {
                    ThreadManager.postToUIThread({
                        sendPatternsToDevice(pattens, position, retryCount + 1, callback)
                    }, 10)
                } else {
                    callback.onFail(code, msg)
                }
            }
        })
    }

    private fun putSendPatternToDeviceCmd(pattern: ByteArray, callback: AsyncCallback<Any>) {
        CmdQueue.putSyncInfoCmd(
                Cmd(pattern, DeviceStateHelper.generateResultCmd(BleCmd.SET_PATTERN), retry = false, callback = object : BleRequestCallback {
                    override fun onResponse(data: ByteArray, hexString: String) {
                        LogManager.bleRequestStatusLog("请求蓝牙状态成功,cmd: $hexString")
                        LogUtils.d(hexString)
                        if (hexString.endsWith(BleCmd.RESPONSE_CODE_SUCCESS)) {
                            callback.onSuccess()
                        } else {
                            callback.onFail(1, "error unknown")
                        }
                    }

                    override fun onFail(code: Int, msg: String) {
                        LogManager.bleRequestStatusLog("请求蓝牙状态失败,pattern code: $code msg: $msg")
                        callback.onFail(code, msg)
                    }
                }))
    }

    /**
     * 发送单条pattern数据到device
     */
    private fun sendPatternToDevice(pattern: ByteArray, callback: AsyncCallback<Any>) {
        putSendPatternToDeviceCmd(pattern, callback)
    }
}