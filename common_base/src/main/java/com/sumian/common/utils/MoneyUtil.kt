package com.sumian.common.utils

import java.math.BigDecimal

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/18 17:24
 * desc   :
 * version: 1.0
 */
class MoneyUtil {
    companion object {

        fun fenToYuanString(fen: Long, includeSign: Boolean = false, includeYuanMark: Boolean = false): String {
            val unsignedFen = Math.abs(fen)
            val sb = StringBuilder()
            if (includeYuanMark) {
                sb.append("￥")
            }
            if (includeSign) {
                sb.append(if (fen > 0) "+" else "-")
            }
            sb.append((unsignedFen / 100).toString())
            sb.append(".")
            sb.append(String.format("%02d", Math.abs(unsignedFen) % 100))
            return sb.toString()
        }

        fun fenToYuanStringWithSign(fen: Long): String {
            return fenToYuanString(fen, true)
        }

        fun yuanStringToFen(yuanString: String?): Long {
            var fen = 0L
            if (yuanString == null || yuanString.isEmpty()) {
                return fen
            }
            try {
                fen = BigDecimal(yuanString).multiply(BigDecimal(100L)).toLong()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return fen
        }
    }
}