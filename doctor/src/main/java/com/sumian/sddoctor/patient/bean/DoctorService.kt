package com.sumian.sddoctor.patient.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DoctorService(var name: String?,
                         var picture: String?) : Parcelable