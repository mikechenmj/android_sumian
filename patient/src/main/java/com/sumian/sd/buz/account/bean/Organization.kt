package com.sumian.sd.buz.account.bean

import com.google.gson.annotations.SerializedName

data class Organization(
        var id: Int,
        var name: String,
        var code: String,
        @SerializedName("im_id")
        var imId: String,
        @SerializedName("organizable_id")
        var organizableId: Int,
        @SerializedName("organizable_type")
        var organizableType: String,
        @SerializedName("created_at")
        var createdAt: Int,
        @SerializedName("updated_at")
        var updatedAt: Int,
        var prefix: String,
        var organizable: Organizable
)

data class Organizable(var id: Int,
                       var name: String,
                       var code: String,
                       var features: Features
)

data class Features(var basic: Boolean,
                    var research: Boolean,
                    var activity: Boolean,
                    @SerializedName("doctor_patient_messages")
                    var doctorPatientMessages: Boolean,
                    var scenario: Boolean,
                    @SerializedName("bed_map")
                    var bedMap: Boolean,
                    var report: Boolean,
                    @SerializedName("redeem_code")
                    var redeemCode: Boolean,
                    var booking: Boolean
)