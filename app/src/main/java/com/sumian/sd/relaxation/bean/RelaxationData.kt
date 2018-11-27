package com.sumian.sd.relaxation.bean

import com.google.gson.annotations.SerializedName

data class RelaxationData(
        @SerializedName("cover")
        val cover: String, // https://sleep-doctor-test.oss-cn-shenzhen.aliyuncs.com/cbti/relaxation/01b46ebf-3624-40ae-a327-6c8e1c56de53.png
        @SerializedName("description")
        val description: String, // 放松训练
        @SerializedName("id")
        val id: Int, // 6
        @SerializedName("name")
        val name: String, // 放松训练

        @SerializedName("audio")
        val audio: String?, // https://sleep-doctor.oss-cn-shenzhen.aliyuncs.com/cbti/relaxation/2a6ecf73-7b32-4d6d-8c68-9cb6d3a70f7a.mp3
        @SerializedName("background")
        val background: String? // https://sleep-doctor-test.oss-cn-shenzhen.aliyuncs.com/cbti/relaxation/35341497-c930-41da-b631-124040a8b34d.jpg

)