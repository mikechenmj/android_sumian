package com.sumian.device.util

import android.os.*
import android.util.Log
import com.clj.fastble.utils.HexUtil
import java.util.concurrent.PriorityBlockingQueue

object CmdQueue {
    private var mPutCmdHandler: Handler? = null
    private var mPutCmdThread: HandlerThread? = null
    private var mSyncSleepDataQueue: PriorityBlockingQueue<Cmd> = PriorityBlockingQueue()
    private var mQueryInfoQueue: PriorityBlockingQueue<Cmd> = PriorityBlockingQueue()
    private var mQueryInfoHandler: Handler? = null
    private var mQueryInfoThread: HandlerThread? = null
    private var mCurrentQueryInfoCmd: Cmd? = null
    private var mCurrentRetryQueryInfoCmd: Cmd? = null
    @Volatile
    private var mIsBlockQueryInfoQueue = false
    @Volatile
    private var mIsBlockSyncSleepData = false

    const val MSG_SEND = 1
    const val MSG_TIMEOUT = 2
    const val MSG_ON_READ = 3
    const val MSG_ON_WRITE = 4

    private const val CMD_RETRY_TIME = 3
    private const val TIME_QUERY_INFO_TIMEOUT = 3000L
    const val EXTRA_CMD_HEX_STRING = "extra_cmd_hex_string"
    const val EXTRA_CMD_HEX_ARRAY = "extra_cmd_hex_array"
    const val EXTRA_CMD_WRITE_RESULT = "extra_cmd_write_result"

    fun init() {
        startQueryInfoQueue()
        mPutCmdThread = HandlerThread("putQueryInfo")
        mPutCmdThread!!.start()
        mPutCmdHandler = Handler(mPutCmdThread!!.looper)
    }

    private fun startQueryInfoQueue() {
        mQueryInfoThread = HandlerThread("queryInfo")
        mQueryInfoThread!!.start()
        mQueryInfoHandler = object : Handler(mQueryInfoThread!!.looper) {
            override fun handleMessage(msg: Message?) {
                when (msg?.what) {
                    MSG_SEND -> {
                        var cmd = takeQueryInfoCmd()
                        Log.i("MCJ", "QUERY: ${HexUtil.formatHexString(cmd.cmd)}")
                        mCurrentQueryInfoCmd = cmd
                        mCurrentRetryQueryInfoCmd = cmd.copy(resultCmds = mutableListOf<ByteArray>().apply {
                            addAll(cmd.resultCmds)
                        })
                        sendMsgQueryInfoTimeOut()

                        /**need
                        DeviceManager.writeData(cmd.cmd)
                         **/

                        /**test start**/
                        mQueryInfoHandler?.sendMessageDelayed(Message.obtain().apply {
                            what = MSG_ON_WRITE
                            obj = Bundle().apply {
                                putByteArray(EXTRA_CMD_HEX_ARRAY, cmd.cmd)
                                putString(EXTRA_CMD_HEX_STRING, HexUtil.formatHexString(cmd.cmd))
                                putBoolean(EXTRA_CMD_WRITE_RESULT, true)
                            }
                        }, (Math.random() * 3000).toLong())
                        /**test end**/
                    }
                    MSG_TIMEOUT -> {
                        Log.i("MCJ", "TIMEOUT: ${HexUtil.formatHexString(mCurrentQueryInfoCmd?.cmd)}")
                        var currentCmd = mCurrentQueryInfoCmd
                        if (currentCmd == null) {
                            sendMsgQueryInfo()
                            return
                        }
                        mCurrentQueryInfoCmd = null
                        var retryCmd = mCurrentRetryQueryInfoCmd
                        if (retryCmd == null) {
                            sendMsgQueryInfo()
                            return
                        }
                        if (retryCmd.retry) {
                            retryCmd(retryCmd)
                        } else {
                            sendMsgQueryInfo()
                        }
                    }
                    MSG_ON_WRITE -> {
                        var currentCmd: Cmd? = mCurrentQueryInfoCmd ?: return
                        var currentCmdHeader = HexUtil.formatHexString(currentCmd!!.cmd).substring(2, 4)
                        var bundle = msg.obj as Bundle
                        var cmd = bundle.getByteArray(EXTRA_CMD_HEX_ARRAY)
                        var cmdStr = bundle.getString(EXTRA_CMD_HEX_STRING)
                        var cmdHeader = cmdStr.substring(2, 4)
                        var success = bundle.getBoolean(EXTRA_CMD_WRITE_RESULT)
                        if (cmdHeader == currentCmdHeader) {
                            Log.i("MCJ", "WRITE: ${HexUtil.formatHexString(mCurrentQueryInfoCmd?.cmd)}")
                            if (success) {
                                if (currentCmd!!.resultCmds.isEmpty()) {
                                    mCurrentQueryInfoCmd = null
                                    sendMsgQueryInfo()
                                } else {
                                    sendMsgQueryInfoTimeOut()
                                    /**test start**/
                                    mQueryInfoHandler?.sendMessageDelayed(Message.obtain().apply {
                                        what = MSG_ON_READ
                                        obj = Bundle().apply {
                                            putByteArray(EXTRA_CMD_HEX_ARRAY, cmd)
                                            putString(EXTRA_CMD_HEX_STRING, cmdStr)
                                        }
                                    }, (Math.random() * 3000).toLong())
                                    /**test end**/
                                }
                            } else {
                                var retryCmd = mCurrentRetryQueryInfoCmd ?: return
                                retryCmd(retryCmd)
                            }
                        }
                    }
                    MSG_ON_READ -> {
                        var currentCmd: Cmd? = mCurrentQueryInfoCmd ?: return
                        var currentCmdHeader = HexUtil.formatHexString(currentCmd!!.cmd).substring(2, 4)
                        var bundle = msg.obj as Bundle
                        var cmd = bundle.getString(EXTRA_CMD_HEX_ARRAY)
                        var cmdStr = bundle.getString(EXTRA_CMD_HEX_STRING)
                        var cmdHeader = cmdStr?.substring(2, 4)
                        if (cmdHeader == currentCmdHeader) {
                            Log.i("MCJ", "READ: ${HexUtil.formatHexString(mCurrentQueryInfoCmd?.cmd)}")
                            var resultCmds = currentCmd.resultCmds
                            if (resultCmds.isEmpty()) {
                                mCurrentQueryInfoCmd = null
                                sendMsgQueryInfo()
                            } else {
                                sendMsgQueryInfoTimeOut()
                                for (resultCmd in resultCmds) {
                                    Log.i("MCJ", "resultCmd: ${HexUtil.formatHexString(resultCmd)}")
                                }
                                if (HexUtil.formatHexString(resultCmds[0]).substring(2, 4).startsWith(cmdHeader)) {
                                    resultCmds.removeAt(0)
                                }
                                Log.i("MCJ", "currentCmd.resultCmds.size: ${currentCmd.resultCmds.size}")
                                if (resultCmds.isEmpty()) {
                                    Log.i("MCJ", "isEmpty")
                                    mCurrentQueryInfoCmd = null
                                    sendMsgQueryInfo()
                                }
                            }
                        }
                    }
                }
            }
        }
        Log.i("MCJ", "start query info")
        /** need
        DeviceManager.registerBleCommunicationWatcher(object : BleCommunicationWatcher {
        override fun onRead(data: ByteArray, hexString: String) {
        mQueryInfoHandler?.sendMessage(Message.obtain().apply {
        what = MSG_ON_READ
        obj = Bundle().apply {
        putByteArray(EXTRA_CMD_HEX_ARRAY, data)
        putString(EXTRA_CMD_HEX_STRING, hexString)
        }
        })
        }

        override fun onWrite(data: ByteArray, hexString: String, success: Boolean, errorMsg: String?) {
        mQueryInfoHandler?.sendMessage(Message.obtain().apply {
        what = MSG_ON_WRITE
        obj = Bundle().apply {
        putByteArray(EXTRA_CMD_HEX_ARRAY, data)
        putString(EXTRA_CMD_HEX_STRING, hexString)
        putBoolean(EXTRA_CMD_WRITE_RESULT, success)
        }
        })
        }
        })
         **/
        sendMsgQueryInfo()
    }

    private fun retryCmd(cmd: Cmd) {
        Log.i("MCJ", "retryCmd ${HexUtil.formatHexString(cmd.cmd)}: $cmd")
        if (cmd.retryTime < CMD_RETRY_TIME) {
            cmd.retryTime += 1
            cmd.priority = Cmd.Priority.RETRY
            mQueryInfoQueue.put(cmd)
        }
        sendMsgQueryInfo()
    }

    private fun sendMsgQueryInfo() {
        removeMsgQueryInfoTimeOut()
        mQueryInfoHandler?.removeMessages(MSG_SEND)
        mQueryInfoHandler?.sendEmptyMessage(MSG_SEND)
    }

    private fun sendMsgQueryInfoTimeOut() {
        removeMsgQueryInfoTimeOut()
        mQueryInfoHandler?.sendEmptyMessageDelayed(MSG_TIMEOUT, TIME_QUERY_INFO_TIMEOUT)
    }

    private fun removeMsgQueryInfoTimeOut() {
        mQueryInfoHandler?.removeMessages(MSG_TIMEOUT)
    }

    private fun putQueryInfoCmd(cmd: Cmd) {
        mPutCmdHandler?.post {
            SystemClock.sleep(10)
            cmd.timeMill = System.currentTimeMillis()
            mQueryInfoQueue.put(cmd)
        }
    }

    private fun putSyncSleepDataCmd(cmd: Cmd) {
        mPutCmdHandler?.post {
            SystemClock.sleep(10)
            cmd.timeMill = System.currentTimeMillis()
            mSyncSleepDataQueue.put(cmd)
        }
    }

    fun putCmd(type: CmdType, cmd: Cmd) {
        if (type === CmdType.QUERY_INFO) {
            putQueryInfoCmd(cmd)
        } else if (type === CmdType.SYNC_SLEEP_DATA) {
            putSyncSleepDataCmd(cmd)
        }
    }

    fun takeQueryInfoCmd(): Cmd {
        while (mIsBlockQueryInfoQueue) {
        }
        return mQueryInfoQueue.take()
    }

    fun takeSyncSleepDataCmd(): Cmd {
        return mSyncSleepDataQueue.take()
    }

    fun blockQueryInfo(block: Boolean) {
        mIsBlockQueryInfoQueue = block
    }

    fun getIsBlockQueryInfo(): Boolean {
        return mIsBlockQueryInfoQueue
    }

    fun blockSyncSleepData(block: Boolean) {
        mIsBlockSyncSleepData = block
    }

    fun getIsBlockSyncSleepData(): Boolean {
        return mIsBlockSyncSleepData
    }

    enum class CmdType {
        SYNC_SLEEP_DATA, QUERY_INFO
    }
}