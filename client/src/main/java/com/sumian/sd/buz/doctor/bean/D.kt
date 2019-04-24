package com.sumian.sd.buz.doctor.bean

import com.google.gson.annotations.SerializedName

data class D(
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
        @SerializedName("services")
        val services: List<Service>,
        @SerializedName("title")
        val title: String, // 主任医师
        @SerializedName("type")
        val type: Int // 0
) {
    data class Service(
            @SerializedName("banner_type")
            val bannerType: Int, // 1
            @SerializedName("description")
            val description: String, // <p><span style="font-size: 16px;">电话咨询服务的服务介绍</span></p>
            @SerializedName("icon")
            val icon: String, // https://sleep-doctor-test.oss-cn-shenzhen.aliyuncs.com/doctors/service/4/902f9881-e1ae-42b4-8458-f28e005bb226.jpg
            @SerializedName("id")
            val id: Int, // 4
            @SerializedName("introduction")
            val introduction: String, // 电话咨询服务的简介
            @SerializedName("name")
            val name: String, // 电话咨询服务
            @SerializedName("picture")
            val picture: String, // https://sleep-doctor-test.oss-cn-shenzhen.aliyuncs.com/doctors/service/4/d49563c6-f1c0-49cb-995f-4053f9264faa.jpg
            @SerializedName("service_packages")
            val servicePackages: List<ServicePackage>,
            @SerializedName("type")
            val type: Int, // 2
            @SerializedName("type_string")
            val typeString: String, // call_advisory
            @SerializedName("video")
            val video: String // https://sleep-doctor-test.oss-cn-shenzhen.aliyuncs.com/doctors/service/4/f915e8de-9693-4893-8897-4c1d0b40f7a6.mp4
    ) {
        data class ServicePackage(
                @SerializedName("bonus")
                val bonus: Int, // 0
                @SerializedName("created_at")
                val createdAt: Int, // 1534736885
                @SerializedName("default_price")
                val defaultPrice: Int, // 100
                @SerializedName("enable")
                val enable: Int, // 1
                @SerializedName("id")
                val id: Int, // 49
                @SerializedName("introduction")
                val introduction: String, // 一分钟电话咨询简介
                @SerializedName("name")
                val name: String, // 一分钟电话咨询
                @SerializedName("packages")
                val packages: List<Package>,
                @SerializedName("service_id")
                val serviceId: Int, // 4
                @SerializedName("service_length")
                val serviceLength: Int, // 1
                @SerializedName("service_length_unit")
                val serviceLengthUnit: Int, // 1
                @SerializedName("sold_by_doctor")
                val soldByDoctor: Int, // 1
                @SerializedName("type")
                val type: Int, // 0
                @SerializedName("updated_at")
                val updatedAt: Int // 1542001629
        ) {
            data class Package(
                    @SerializedName("base_unit_price")
                    val baseUnitPrice: Int, // 100
                    @SerializedName("bonus")
                    val bonus: Int, // 0
                    @SerializedName("created_at")
                    val createdAt: Int, // 1535506639
                    @SerializedName("discount_amount")
                    val discountAmount: Int, // 0
                    @SerializedName("doctor_id")
                    val doctorId: Int, // 38
                    @SerializedName("enable")
                    val enable: Int, // 1
                    @SerializedName("id")
                    val id: Int, // 1893
                    @SerializedName("owner_id")
                    val ownerId: Int, // 38
                    @SerializedName("owner_type")
                    val ownerType: String, // App\Models\Doctor
                    @SerializedName("package_no")
                    val packageNo: String, // 190307634998
                    @SerializedName("service_package_enable")
                    val servicePackageEnable: Int, // 1
                    @SerializedName("service_package_id")
                    val servicePackageId: Int, // 49
                    @SerializedName("template_id")
                    val templateId: Int, // 49
                    @SerializedName("template_type")
                    val templateType: String, // App\Models\ServicePackage
                    @SerializedName("unit_price")
                    val unitPrice: Int, // 100
                    @SerializedName("updated_at")
                    val updatedAt: Int // 1554883836
            )
        }
    }
}