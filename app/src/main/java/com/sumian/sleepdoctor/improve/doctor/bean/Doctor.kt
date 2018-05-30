package com.sumian.sleepdoctor.improve.doctor.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 * 医生信息
 */
@Parcelize
data class Doctor(var id: Int = 0,
                  var name: String?,
                  var avatar: String?,
                  var hospital: String?,
                  var department: String?,
                  var title: String?,
                  var introduction: String?,
                  var introduction_no_tag: String?,
                  var services: ArrayList<DoctorService>?) : Parcelable, Serializable