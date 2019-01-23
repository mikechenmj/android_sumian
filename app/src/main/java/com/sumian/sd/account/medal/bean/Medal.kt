package com.sumian.sd.account.medal.bean

import com.google.gson.annotations.SerializedName

/**
 * Created by jzz
 *
 * on 2019/1/22
 *
 * desc:
 */
data class Medal(val id: Int,
                 val icon: String,
                 @SerializedName("gain_medal_picture") val gainMedalPicture: String,
                 @SerializedName("title") val tips: String,
                 val url: String) {
}