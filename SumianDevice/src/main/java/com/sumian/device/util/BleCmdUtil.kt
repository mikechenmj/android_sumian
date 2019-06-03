package com.sumian.device.util

import com.clj.fastble.utils.HexUtil
import com.sumian.device.cmd.BleCmd

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/9 11:42
 * desc   :
 * version: 1.0
 */

class BleCmdUtil {

    companion object {
        fun createDataFromBytes(cmdType: String, data: ByteArray? = null): ByteArray {
            val sb = StringBuilder()
            sb.append(BleCmd.APP_CMD_HEADER)
            sb.append(cmdType)
            if (data != null) {
                sb.append(String.format("%02X", data.size))
                sb.append(HexUtil.encodeHexStr(data))
            }
            return HexUtil.hexStringToBytes(sb.toString())
        }

        fun createDataFromString(cmdType: String, content: String? = null): ByteArray {
            return createDataFromBytes(cmdType, HexUtil.hexStringToBytes(content))
        }

        /**
         * [aa][bb][cc][dd]
         * aa: 1 byte 55/aa
         * bb: 1 byte cmd type
         * cc: 1 byte data length
         * dd: cc byte, data content
         */
        fun getContentFromData(data: ByteArray): ByteArray? {
            return if (data.size <= 3) {
                null
            } else {
                data.copyOfRange(3, data.size)
            }
        }

        fun getContentFromData(data: String): String? {
            return if (data.length <= 6)
                null
            else
                data.substring(6)
        }

        fun isDeviceCmdValid(hexString: String?): Boolean {
            return hexString != null
                    && hexString.length >= 4
                    && hexString.substring(0, 2) == BleCmd.MONITOR_CMD_HEADER
        }

        fun getCmdType(hexString: String): String {
            return hexString.substring(2, 4)
        }

        fun stringToCharBytes(s: String): ByteArray {
            val bytes = ByteArray(s.length)
            val cs = s.toCharArray()
            for ((index, c) in cs.withIndex()) {
                bytes[index] = c.toByte()
            }
            return bytes
        }

        fun hexStringToLong(s: String): Long {
            return java.lang.Long.parseLong(s, 16)
        }

        fun createSuccessResponse(cmdType: String): ByteArray {
            return createDataFromString(cmdType, BleCmd.RESPONSE_CODE_SUCCESS)
        }

        fun createNotSupportResponse(cmdType: String): ByteArray {
            return createDataFromString(cmdType, BleCmd.RESPONSE_CODE_UNSUPPORT_CMD)
        }
    }

}