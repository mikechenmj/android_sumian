package com.sumian.sd.homepage.bean

data class SleepPrescriptionStatus(
        val data: List<Data>,
        val meta: Meta
)

data class Data(
        val id: Int,
        val date: Int
)

data class Meta(
        val real_sleep_duration_avg: Int,
        val stop_service: Boolean,
        val prescription: Prescription,
        val enquire: Boolean,
        val update: Boolean,
        val keep: Boolean
)

data class Prescription(
        val status: Boolean,
        val data: Any,
        val sleep_duration_avg_new: Any
)