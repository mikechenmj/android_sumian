package com.sumian.sddoctor.service.advisory.bean

import android.text.TextUtils
import com.sumian.sddoctor.R
import com.sumian.sddoctor.util.ResUtils

/**
 * 医生信息
 */
data class Doctor(var id: Int = 0,
                  var hospital: String?,
                  var department: String?,
                  var title: String?,
                  var introduction: String?,
                  var name: String?,
                  var retailer_id: Int,
                  var invitation_code: String?,
                  var avatar: String?,
                  var mobile: String?,
                  var qr_code: String?,
                  var qr_code_raw: String?,
                  var mobile_notice: Int,
                  var notify_mobile: String?,
                  var created_at: String?,
                  var updated_at: String?,
                  var introduction_no_tag: String?
) {


    fun formatName(): String {
        return if (TextUtils.isEmpty(name)) ResUtils.getString(R.string.sleep_doctor) else name!!
    }

    fun getDoctorReplyName(): String {
        return ResUtils.getString(R.string.my_reply)
    }
}
