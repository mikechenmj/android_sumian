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
        fun hasNewVersion(latestVersion: IntArray, currentVersion: IntArray): Boolean {
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
    }
}