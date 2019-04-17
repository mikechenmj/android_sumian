package com.sumian.sddoctor.login.login.bean

import android.content.Context
import android.os.Parcelable
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
        var id: Int,
        var retailer_id: Int,
        var invitation_code: String,
        var hospital: String,
        var department: String,
        var title: String,
        var introduction: String,
        var name: String,
        var nickname: String? = null,
        var avatar: String,
        var mobile: String,
        var qr_code: String,
        var qr_code_raw: String,
        var mobile_notice: Int,
        var notify_mobile: String,
        var created_at: String,
        var updated_at: String,
        var socialite: MutableList<SocialiteInfo>,
        var introduction_no_tag: String,
        var bind_patient: Int,
        var review_status: Int, //审核状态 0:未认证 1:审核中 2:已认证
        var set_password: Boolean = false,
        var im_id: String? = null,
        var im_password: String? = null,
        var identity: Int = 0
) : Parcelable {
    companion object {
        const val AUTHENTICATION_STATE_NOT_AUTHENTICATED = 0
        const val AUTHENTICATION_STATE_IS_AUTHENTICATING = 1
        const val AUTHENTICATION_STATE_AUTHENTICATED = 2

    }

    fun getAuthenticationState(): Int {
        return review_status
    }

    fun getAuthenticationString(): String {
        return App.getAppContext().getString(when (getAuthenticationState()) {
            AUTHENTICATION_STATE_NOT_AUTHENTICATED -> R.string.not_authenticated
            AUTHENTICATION_STATE_IS_AUTHENTICATING -> R.string.waiting_authentication
            else -> R.string.authenticated
        })
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
                return hospital
            }
        }
    }

    private fun isDoctor() = identity == 0
}