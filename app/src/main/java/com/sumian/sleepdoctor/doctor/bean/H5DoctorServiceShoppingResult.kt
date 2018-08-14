package com.sumian.sleepdoctor.doctor.bean

import com.google.gson.annotations.SerializedName

data class H5DoctorServiceShoppingResult(
        @SerializedName("service") val service: DoctorService,
        @SerializedName("packageId") val packageId: Int
)