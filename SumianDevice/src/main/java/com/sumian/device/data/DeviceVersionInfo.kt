package com.sumian.device.data

import com.google.gson.annotations.SerializedName

data class DeviceVersionInfo(
        @SerializedName("monitor")
        val monitor: VersionInfo?,
        @SerializedName("sleeper")
        val sleeper: VersionInfo?
) {
    data class VersionInfo(
            @SerializedName("md5")
            val md5: String, // 0ea35fec52ac3c41ba8a36654d45b434
            @SerializedName("show_update_mode")
            val showUpdateMode: Int, // 0
            @SerializedName("url")
            val url: String, // https://sumian-test.oss-cn-shenzhen.aliyuncs.com/firmware/20190505/f431992b-db33-4b6e-9a3c-8c251fdd543d.zip
            @SerializedName("version")
            val version: String // 9.5.9
    )
}