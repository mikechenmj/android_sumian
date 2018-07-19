package com.sumian.sleepdoctor.homepage.bean

import com.google.gson.annotations.SerializedName

data class SleepPrescriptionWrapper(
        @SerializedName("sleep_prescription") val sleepPrescription: SleepPrescription?,
        @SerializedName("update") val showUpdateDialog: Boolean,
        @SerializedName("enquire") var showEnquireDialog: Boolean,
        @SerializedName("stop_service") val isServiceStopped: Boolean,
        @SerializedName("former") val former: Boolean // 沿用之前的的
)