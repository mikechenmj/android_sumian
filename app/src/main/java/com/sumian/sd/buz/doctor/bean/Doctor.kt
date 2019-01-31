package com.sumian.sd.buz.doctor.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 * 医生信息
 */
@Parcelize
data class Doctor(var id: Int = 0,
                  var hospital: String?,
                  var department: String?,
                  var title: String?,
                  var qr_code_raw: String?,
                  var introduction: String?,
                  var introduction_no_tag: String?,
                  var name: String?,
                  var avatar: String?,
                  var mobile: String?,
                  var mobile_notice: Int,
                  var notify_mobile: String?,
                  var created_at: String?,
                  var updated_at: String?,
                  var services: ArrayList<DoctorService>?) : Parcelable, Serializable
