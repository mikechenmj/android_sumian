package com.sumian.sddoctor.login.login.bean

import android.content.Context
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.sumian.sddoctor.BuildConfig
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.App
import kotlinx.android.parcel.Parcelize

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/18 19:07
 *     desc   :
 *     version: 1.0
 * </pre>
 */
@Parcelize
data class DoctorInfo(
        @SerializedName("avatar")
        var avatar: String, // https://sleep-doctor-dev.oss-cn-shenzhen.aliyuncs.com/avatar/default/avatar_doctor.png
        @SerializedName("bind_notice_enable")
        var bindNoticeEnable: Int, // 1
        @SerializedName("bind_patient")
        var bindPatient: Int, // 0
        @SerializedName("cases_time")
        var casesTime: Int, // 0
        @SerializedName("created_at")
        var createdAt: String, // 2019-04-19 16:17:15
        @SerializedName("department")
        var department: String,
        @SerializedName("experience")
        var experience: Int, // 从业经验 0:1年 1:2年 2:3年 3:4年 4:5年及以上 当type=1时必须
        @SerializedName("hospital")
        var hospital: String,
        @SerializedName("id")
        var id: Int, // 86
        @SerializedName("im_id")
        var imId: String?, // develop_d_be4a95ec-048d-4fac-af5d-b7d7e5ab439c
        @SerializedName("im_password")
        var imPassword: String?, // 9b00663937d7fcb532353f29d5de70cd
        @SerializedName("introduction")
        var introduction: String, // <p></p>
        @SerializedName("introduction_no_tag")
        var introductionNoTag: String,
        @SerializedName("invitation_code")
        var invitationCode: String, // 0QY84X
        @SerializedName("last_login_at")
        var lastLoginAt: String, // 2019-04-19 16:20:08
        @SerializedName("mobile")
        var mobile: String, // 13570461901
        @SerializedName("mobile_notice")
        var mobileNotice: Int, // 0
        @SerializedName("name")
        var name: String, // 医生522
        @SerializedName("notify_mobile")
        var notifyMobile: String,
        @SerializedName("qr_code")
        var qrCode: String, // https://sd-dev-oss-cdn.sumian.com/doctors/qr_code/doctor_qr_bg_86_1555661837.png
        @SerializedName("qr_code_raw")
        var qr_code_raw: String, // https://sd-dev-oss-cdn.sumian.com/doctors/qr_code/doctor_qr_86_1555661836.png
        @SerializedName("qualification")
        var qualification: String,
        @SerializedName("retailer_id")
        var retailerId: Int, // 0
        @SerializedName("review_status")
        var review_status: Int, // 审核状态 0:未认证 1:审核中 2:已认证
        @SerializedName("set_password")
        var setPassword: Boolean, // false
        @SerializedName("socialite")
        var socialite: MutableList<SocialiteInfo>,
        @SerializedName("title")
        var title: String,
        @SerializedName("type")
        var type: Int, // 0:医生 1:咨询师 默认:0
        @SerializedName("updated_at")
        var updatedAt: String // 2019-04-19 16:20:27,
) : Parcelable {
    companion object {
        const val AUTHENTICATION_STATE_NOT_AUTHENTICATED = 0
        const val AUTHENTICATION_STATE_IS_AUTHENTICATING = 1
        const val AUTHENTICATION_STATE_AUTHENTICATED = 2
        const val TYPE_DOCTOR = 0
        const val TYPE_COUNSELOR = 1

    }

    fun getAuthenticationState(): Int {
        return review_status
    }

    fun isAuthenticated(): Boolean {
        return review_status == AUTHENTICATION_STATE_AUTHENTICATED
    }

    fun getAuthenticationString(): String {
        return App.run {
            getAppContext().getString(when (getAuthenticationState()) {
                AUTHENTICATION_STATE_NOT_AUTHENTICATED -> R.string.not_authenticated
                AUTHENTICATION_STATE_IS_AUTHENTICATING -> R.string.waiting_authentication
                else -> R.string.authenticated
            })
        }
    }

    fun isVisitorAccount(): Boolean {
        return mobile == BuildConfig.VISITOR_MOBILE
    }

    fun getIdentityString(context: Context): String {
        return context.getString(if (isDoctor()) R.string.doctor else R.string.counselor)
    }

    fun getDescString(context: Context): String {
        if (review_status == AUTHENTICATION_STATE_AUTHENTICATED) {
            return context.getString(if (isDoctor()) R.string.doctor else R.string.counselor)
        } else {
            return if (isDoctor()) {
                return hospital
            } else {
                return qualification
            }
        }
    }

    fun getExperienceString(context: Context): String? {
        if (!isAuthenticated()) {
            return null
        }
        return context.resources.getStringArray(R.array.counselor_experience_years)[experience]
    }

    fun isDoctor() = type == 0

    fun getSocialiteNoneNull(): MutableList<SocialiteInfo> {
        return if (socialite == null) ArrayList() else socialite!!
    }
}