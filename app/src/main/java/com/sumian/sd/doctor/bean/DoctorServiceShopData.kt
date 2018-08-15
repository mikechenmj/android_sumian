package com.sumian.sd.doctor.bean

import com.google.gson.annotations.SerializedName

data class DoctorServiceShopData(
        @SerializedName("service") val service: DoctorService,
        @SerializedName("packageId") val packageId: Int
)