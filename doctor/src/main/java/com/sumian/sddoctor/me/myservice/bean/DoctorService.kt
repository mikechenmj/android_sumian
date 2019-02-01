package com.sumian.sddoctor.me.myservice.bean

data class DoctorService(
        val id: Int,
        val type: Int,
        val name: String,
        val description: String,
        val introduction: String,
        val banner_type: Int,
        val picture: String,
        val video: String,
        val icon: String,
        val is_opened: Boolean,
        val type_string: String,
        val servicePackages: List<ServicePackage>
) {

    companion object {
        const val SERVICE_TYPE_SLEEP_REPORT: Int = 0
        const val SERVICE_TYPE_ADVISORY: Int = 1
        const val SERVICE_TYPE_PHONE_ADVISORY: Int = 2
        const val SERVICE_TYPE_CBTI: Int = 3
    }

}