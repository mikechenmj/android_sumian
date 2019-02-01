package com.sumian.sddoctor.util

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/9/29 11:19
 * desc   :
 * version: 1.0
 */
class BooleanUtil {
    companion object {
        fun getBooleanFromInt(int: Int): Boolean {
            return int == 1
        }

        fun getIntFromBoolean(b: Boolean): Int {
            return if (b) 1 else 0
        }
    }
}