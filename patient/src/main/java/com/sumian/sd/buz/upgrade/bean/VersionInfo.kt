package com.sumian.sd.buz.upgrade.bean


import com.google.gson.annotations.SerializedName

data class VersionInfo(
        @SerializedName("description")
        val description: String, // 强制升级
        @SerializedName("md5")
        val md5: String, // e823a25816b5aad9a2580f413ec06736
        @SerializedName("show_update_mode")
        val showUpdateMode: Int, // 0:推荐更新 1:强制更新
        @SerializedName("url")
        val url: String, // https://sumian-test.oss-cn-shenzhen.aliyuncs.com/firmware/20190617/c084b62a-6729-40f4-924b-56277fd04bdf.zip
        @SerializedName("version")
        val version: String // 9.6.3
) {
    fun isForceUpdate(): Boolean {
        return showUpdateMode == 1
    }
}