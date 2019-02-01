package com.sumian.sddoctor.patient.bean

import com.google.gson.annotations.SerializedName

data class MedicalRecord(
        @SerializedName("id") val id: Int?,
        @SerializedName("user_id") val userId: Int?,
        @SerializedName("allergy") val allergy: List<String>?,
        @SerializedName("surgery") val surgery: List<String>?,
        @SerializedName("inheritance") val inheritance: List<String>?,
        @SerializedName("created_at") val createdAt: Int?,
        @SerializedName("updated_at") val updatedAt: Int?,
        @SerializedName("user") val user: Patient
) {
    fun getAllergyString(): String {
        return listToString(allergy)
    }

    fun getInheritanceString(): String {
        return listToString(inheritance)
    }

    fun getSurgeryString(): String {
        return listToString(surgery)
    }

    private fun listToString(list: List<String>?): String {
        if (list == null || list.isEmpty()) {
            return ""
        }
        val sb = StringBuilder()
        for ((index, text) in list.withIndex()) {
            sb.append(text)
            if (index != list.size - 1) {
                sb.append(",")
            }
        }
        return sb.toString()
    }
}