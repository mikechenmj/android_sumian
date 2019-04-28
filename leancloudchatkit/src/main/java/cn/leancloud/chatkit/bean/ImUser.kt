package cn.leancloud.chatkit.bean

import android.text.TextUtils
import com.google.gson.annotations.SerializedName

data class ImUser(
        @SerializedName("avatar")
        val avatar: String, // https://sleep-doctor-dev.oss-cn-shenzhen.aliyuncs.com/avatar/2102/12ed2e8e-ac59-4b84-b437-ee81e57f7007.jpg
        @SerializedName("id")
        val id: Int, // 2102
        @SerializedName("im_id")
        val imId: String, // develop8232e119d8f59aa83050a741631803a6
        @SerializedName("name")
        val name: String?, // testzzz
        @SerializedName("nickname")
        val nickname: String, // test詹徐照
        @SerializedName("real_name")
        val realName: String?,
        @SerializedName("type")
        val type: Int  //0:医生 1:咨询师
) {
    fun getNameOrNickname(): String {
        return if (!TextUtils.isEmpty(realName)) {
            realName!!
        } else if (!TextUtils.isEmpty(name)) {
            name!!
        } else if (!TextUtils.isEmpty(nickname)) {
            nickname!!
        } else {
            "no name"
        }
    }

    fun isDoctor(): Boolean {
        return type == 0
    }

    fun getNameSuffix(): String {
        return if (type == 0) {
            "医生"
        } else {
            "咨询师"
        }
    }
}