package com.sumian.sddoctor.login.register.bean

import com.google.gson.annotations.SerializedName

data class UploadMedicalLicenseResponse(
        @SerializedName("mobile") val mobile: String,
        @SerializedName("name") val name: String,
        @SerializedName("hospital") val hospital: String,
        @SerializedName("department") val department: String,
        @SerializedName("title") val title: String,
        @SerializedName("medical_license_url") val medicalLicenseUrl: String,
        @SerializedName("status") val status: Int,
        @SerializedName("updated_at") val updatedAt: Int,
        @SerializedName("created_at") val createdAt: Int,
        @SerializedName("id") val id: Int
)