package com.sumian.device.util

import com.clj.fastble.utils.HexUtil

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/15 16:28
 * desc   :
 * version: 1.0
 */
class MacUtil {
    companion object {

        private const val MAX_LONG_MAC = 0xFFFFFFFFFFFF
        private const val STRING_MAC_NUMBER_LENGTH = 12

        fun getLongMacFromStringMac(mac: String): Long {
            return java.lang.Long.parseLong(mac.replace(":", ""), 16)
        }

        fun getStringMacFromLong(longMac: Long): String {
            val l = if (longMac > MAX_LONG_MAC) {
                longMac % MAX_LONG_MAC
            } else {
                longMac
            }
            var hexString = java.lang.Long.toHexString(l)
            if (hexString.length < STRING_MAC_NUMBER_LENGTH) {
                hexString = "0".repeat(STRING_MAC_NUMBER_LENGTH - hexString.length) + hexString
            }
            val sb = StringBuilder()
            for (i in 0 until hexString.length step 2) {
                sb.append(hexString.substring(i, i + 2))
                if (i != hexString.length - 2) {
                    sb.append(":")
                }
            }
            return sb.toString().toUpperCase()
        }

        fun getStringMacFromCmdBytes(bytes: ByteArray): String {
            return getStringMacFromLong(getLongMacFromCmdBytes(bytes))
        }

        fun getLongMacFromCmdBytes(bytes: ByteArray): Long {
            val hexString = HexUtil.formatHexString(bytes)
            val data = BleCmdUtil.getContentFromData(hexString)!!
            return java.lang.Long.parseLong(data, 16)
        }
    }
}