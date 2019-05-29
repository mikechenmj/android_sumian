package com.sumian.sd.buz.cbti.bean

import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import com.sumian.sd.R
import com.sumian.sd.app.App

data class Commenter(
        @SerializedName("avatar")
        val avatar: String,
        @SerializedName("nickname")
        val nickname: String
) {


    fun formatNickname(): String =
            if (TextUtils.isEmpty(nickname)) App.getAppContext().getString(R.string.anonymous_nickname) else
                nickname
}