package com.sumian.sd.buz.doctor.bean

import android.content.Context
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.sumian.sd.R
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 * 医生信息
 */
@Parcelize
data class Doctor(
        @SerializedName("avatar")
        val avatar: String, // https://sleep-doctor-test.oss-cn-shenzhen.aliyuncs.com/doctor/avatar/38/bc0b2380-5e57-4ead-8934-d7f6780d3f63.jpg
        @SerializedName("cases_time")
        val casesTime: Int, // 0
        @SerializedName("department")
        val department: String, // ？？？？
        @SerializedName("experience")
        val experience: Int, // 0
        @SerializedName("hospital")
        val hospital: String, // 人民医院。。。。。
        @SerializedName("id")
        val id: Int, // 38
        @SerializedName("introduction")
        val introduction: String, // <p></p>
        @SerializedName("introduction_no_tag")
        val introductionNoTag: String,
        @SerializedName("name")
        val name: String, // test詹徐照？？？？？
        @SerializedName("qr_code_raw")
        val qrCodeRaw: String, // https://sleep-doctor-test.oss-cn-shenzhen.aliyuncs.com/doctors/qr_code/doctor_qr_38_1543825418.png
        @SerializedName("qualification")
        val qualification: String,
        @SerializedName("review_status")
        val reviewStatus: Int, // 2
        @SerializedName("title")
        val title: String, // 主任医师
        @SerializedName("type")
        val type: Int, // 0
        @SerializedName("services")
        var services: ArrayList<DoctorService>?) : Parcelable, Serializable {
    companion object {
        const val AUTHENTICATION_STATE_NOT_AUTHENTICATED = 0
        const val AUTHENTICATION_STATE_IS_AUTHENTICATING = 1
        const val AUTHENTICATION_STATE_AUTHENTICATED = 2
        const val TYPE_DOCTOR = 0
        const val TYPE_COUNSELOR = 1

    }

    fun getAuthenticationState(): Int {
        return reviewStatus
    }

    fun isAuthenticated(): Boolean {
        return reviewStatus == AUTHENTICATION_STATE_AUTHENTICATED
    }

    fun isDoctor() = type == 0

    fun getDesc(context: Context): String {
        return if (type == 0) {
            "${hospital} ${department}"
        } else {
            val years = context.resources.getStringArray(R.array.counselor_experience_years)
            val year = years[Math.min(experience, years.size - 1)]
            context.getString(R.string.counselor_desc, qualification, year, casesTime)
        }
    }
}


@Parcelize
data class DoctorService(var id: Int,
                         var type: Int/*0：睡眠日记，1：图文咨询*/,
                         var name: String,//服务名
                         var description: String, //服务描述
                         var introduction: String, //服务简介
                         var banner_type: Int,//展示位类型 0：图片 1：视频
                         var picture: String,//图片 url banner_type=0
                         var video: String,//视频 url banner_type=1
                         var icon: String,//图标 url
                         var service_packages: ArrayList<DoctorServicePackage> /*参数include=services出现，否则不展示*/
) : Parcelable, Serializable {

    companion object {
        const val SERVICE_TYPE_SLEEP_REPORT: Int = 0
        const val SERVICE_TYPE_ADVISORY: Int = 1
        const val SERVICE_TYPE_PHONE_ADVISORY: Int = 2
        const val SERVICE_TYPE_CBTI: Int = 3
    }
}

@Parcelize
data class DoctorServicePackage(var id: Int,//服务包模板id
                                var service_id: Int,//服务id
                                var name: String,//服务包名称
                                var introduction: String, //服务包简介
                                var default_price: Double, //默认价格
                                var service_length: Int,//服务时长
                                var service_length_unit: Int,//服务时长类型 0：无 1：分钟 2：小时 3：天
                                var enable: Int,//是否开启
                                var sold_by_doctor: Int,//是否通过医生售卖
                                var created_at: Int,//创建时间
                                var updated_at: Int,//最后更新时间
                                var packages: List<ServicePackage>
) : Parcelable, Serializable {

    /**
     * 医生服务包功能下属的各个级别的服务包 e.y.  比如通过不同价格划分了很多小包
     */
    @Parcelize
    data class ServicePackage(var id: Int,//医生的服务包
                              var unit_price: Long,//实际单价
                              var base_unit_price: Long,//基础单价
                              var doctor_id: Int,//医生id
                              var service_package_id: Int,//服务包模板id
                              var enable: Int,//开启 1：开启 已过滤enable=0情况
                              var created_at: Int,//创建时间
                              var updated_at: Int)//最后更新时间
        : Parcelable, Serializable
}

data class H5DoctorServiceShoppingResult(
        @SerializedName("service") val service: DoctorService,
        @SerializedName("packageId") val packageId: Int
)