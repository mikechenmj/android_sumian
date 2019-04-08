package com.sumian.common.utils

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/9/30 9:23
 * desc   :
 * version: 1.0
 */
class VersionUtil {
    companion object {
        fun hasNewVersion(latestVersion: ArrayList<Int>, currentVersion: ArrayList<Int>): Boolean {
            if (currentVersion.size != latestVersion.size) {
                return true
            }
            for (i in 0 until currentVersion.size) {
                return if (latestVersion[i] > currentVersion[i]) {
                    true
                } else if (latestVersion[i] < currentVersion[i]) {
                    false
                } else {
                    continue
                }
            }
            return false
        }

        @JvmStatic
        fun hasNewVersion(latestVersion: List<String>, currentVersion: List<String>): Boolean {
            return hasNewVersion(stringListToIntList(latestVersion), stringListToIntList(currentVersion))
        }

        @JvmStatic
        fun hasNewVersion(latestVersion: String, currentVersion: String): Boolean {
            return hasNewVersion(stringListToIntList(latestVersion.split(".")), stringListToIntList(currentVersion.split(".")))
        }

        fun isVersionZero(version: String): Boolean {
            val split = version.split(".")
            for (c in split) {
                if (c != "0")
                    return false
            }
            return true
        }

        private fun stringListToIntList(sList: List<String>): ArrayList<Int> {
            val iList = ArrayList<Int>()
            for (s in sList) {
                iList.add(s.toInt())
            }
            return iList
        }
    }
}