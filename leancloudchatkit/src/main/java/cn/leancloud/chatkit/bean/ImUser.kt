package cn.leancloud.chatkit.bean

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
        val realName: String?
) {
    fun getNameOrNickname(): String {
        return realName ?: name ?: nickname ?: "no name"
    }
}