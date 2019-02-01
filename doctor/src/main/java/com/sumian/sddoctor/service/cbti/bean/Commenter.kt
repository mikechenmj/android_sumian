package com.sumian.sddoctor.service.cbti.bean

import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.App

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