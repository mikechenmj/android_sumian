package com.sumian.common.buz.chat.bean

import com.google.gson.annotations.SerializedName

data class CreateConversationResponse(
        @SerializedName("conversation_id")
        val conversationId: String, // 5c879e503f4fcf8c75171a12
        @SerializedName("created_at")
        val createdAt: Int, // 1552391760
        @SerializedName("doctor_id")
        val doctorId: Int, // 10
        @SerializedName("id")
        val id: Int, // 1
        @SerializedName("meta")
        val meta: Meta,
        @SerializedName("updated_at")
        val updatedAt: Int, // 1552391760
        @SerializedName("user_id")
        val userId: Int // 2665
) {
    data class Meta(
            @SerializedName("doctor")
            val doctor: Doctor,
            @SerializedName("user")
            val user: User
    ) {
        data class Doctor(
                @SerializedName("avatar")
                val avatar: String, // https://sleep-doctor-dev.oss-cn-shenzhen.aliyuncs.com/doctor/avatar/10/697e6854-7a33-4cf2-bd7e-604e6925423f.jpg
                @SerializedName("id")
                val id: Int, // 10
                @SerializedName("im_id")
                val imId: String, // developd3d9446802a44259755d38e6d163e820
                @SerializedName("name")
                val name: String // test詹徐照
        )

        data class User(
                @SerializedName("avatar")
                val avatar: String, // https://sleep-doctor-dev.oss-cn-shenzhen.aliyuncs.com/avatar/2665/db68ca67-86e1-46ab-ab4e-b749a1db00c2.png
                @SerializedName("id")
                val id: Int, // 2665
                @SerializedName("im_id")
                val imId: String, // develope727fa59ddefcefb5d39501167623132
                @SerializedName("name")
                val name: String, // 王江
                @SerializedName("nickname")
                val nickname: String, // 用户203
                @SerializedName("real_name")
                val realName: String
        )
    }
}