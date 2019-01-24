package com.sumian.sd.account.achievement.bean

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by jzz
 *
 * on 2019/1/24
 *
 * desc:
 */
@Parcelize
data class ShareAchievement(val achievement: Achievement,
                            @SerializedName("created_at")
                            val createdAt: Int,
                            @SerializedName("id")
                            val id: Int,
                            @SerializedName("name")
                            val name: String,
                            @SerializedName("type")
                            val type: Int,
                            @SerializedName("updated_at")
                            val updatedAt: Int,
                            val avatar: String,
                            val qrCode: String
) : Parcelable