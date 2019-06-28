package com.sumian.sd.buz.cbti.bean


import com.google.gson.annotations.SerializedName

data class CbtiDetail(
        @SerializedName("banner_type")
        val bannerType: Int, // 1
        @SerializedName("description")
        val description: String, // <p></p><img src="https://sleep-doctor.oss-cn-shenzhen.aliyuncs.com/admin/pictures/a5be5cef-4f0e-4eed-bad7-a0446a582ae4.jpg" alt="" style="float:none;height: auto;width: auto"/><p></p><img src="https://sleep-doctor.oss-cn-shenzhen.aliyuncs.com/admin/pictures/a6e79beb-46cf-4ac1-b203-453053319fa7.jpg" alt="" style="float:none;height: auto;width: auto"/><p></p><img src="https://sleep-doctor.oss-cn-shenzhen.aliyuncs.com/admin/pictures/943e12a2-9ec6-4a5b-9b1c-41b05b2d874f.jpg" alt="" style="float:none;height: auto;width: auto"/><p></p><img src="https://sleep-doctor.oss-cn-shenzhen.aliyuncs.com/admin/pictures/c29c1018-b76a-4713-86e0-0c204d82e863.jpg" alt="" style="float:none;height: auto;width: auto"/>
        @SerializedName("icon")
        val icon: String, // https://sleep-doctor-dev.oss-cn-shenzhen.aliyuncs.com/doctors/service/5/6d934cbf-74ad-4175-8d4a-635d38a09116.jpg
        @SerializedName("introduction")
        val introduction: String, // 失眠认知行为治疗(CBTi)
        @SerializedName("name")
        val name: String, // 失眠认知行为治疗(CBTi)
        @SerializedName("picture")
        val picture: String, // https://sleep-doctor-dev.oss-cn-shenzhen.aliyuncs.com/doctors/service/5/08a68b86-5769-45ce-b458-274a20aba2b3.jpg
        @SerializedName("video")
        val video: String // https://sleep-doctor-dev.oss-cn-shenzhen.aliyuncs.com/doctors/service/5/c785a03d-8621-4017-ae51-82e1b2d84549.mp4
)