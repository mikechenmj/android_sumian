package com.sumian.sd.homepage.bean

data class SleepPrescriptionStatus(
        val data: List<SleepPrescriptionData>,
        val meta: Meta
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
        val data: SleepPrescription?,
        val sleep_duration_avg_new: Any,
        val days_remain:Int

)