package com.sumian.sleepdoctor.improve.doctor.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class Doctor(var id: Int = 0, var name: String?, var avatar: String?, var hospital: String?,
                  var department: String?, var title: String?, var introduction: String?) : Parcelable, Serializable