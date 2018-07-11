package com.sumian.sleepdoctor.doctor.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 *
 *Created by sm
 * on 2018/5/30 10:44
 * desc:
 **/
@Parcelize
data class DoctorServicePackage(var id: Int,
                                var days: Int,
                                var description: String,
                                var unit_price: Double /*单价，单位：分*/,
                                var not_buy_description: String,
                                var bought_description: String,
                                var price_text: String /*显示定价*/
) : Parcelable, Serializable