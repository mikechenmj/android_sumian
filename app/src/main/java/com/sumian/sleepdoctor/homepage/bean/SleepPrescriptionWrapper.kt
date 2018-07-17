package com.sumian.sleepdoctor.homepage.bean

import com.google.gson.annotations.SerializedName

data class SleepPrescriptionWrapper(
        @SerializedName("sleep_prescription") val sleepPrescription: SleepPrescription?,
        @SerializedName("update") val showUpdateDialog: Boolean,
        @SerializedName("enquire") val showEnquireDialog: Boolean,
        @SerializedName("stop_service") val showStopServiceDialog: Boolean,
        @SerializedName("former") val isServiceStopped: Boolean
)