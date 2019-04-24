package com.sumian.sddoctor.account

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.SPUtils
import com.sumian.sddoctor.login.login.bean.DoctorInfo
import com.sumian.sddoctor.login.login.bean.LoginResponse
import com.sumian.sddoctor.util.JsonUtil

@Suppress("UNUSED_PARAMETER")
/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/14 10:42
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class AccountViewModel(application: Application) : AndroidViewModel(application) {

    private var mTokenInfo: MutableLiveData<TokenInfo> = MutableLiveData()
    private var mDoctorInfo: MutableLiveData<DoctorInfo> = MutableLiveData()
    private var mTokenIsInvalid: MutableLiveData<Boolean> = MutableLiveData()

    init {
        val tokenInfoJson = SPUtils.getInstance().getString(KEY_TOKEN_INFO)
        val tokenInfo = JsonUtil.fromJson(tokenInfoJson, TokenInfo::class.java)
        updateTokenInfo(tokenInfo, false)
        val doctorInfoJson = SPUtils.getInstance().getString(KEY_DOCTOR_INFO)
        val doctorInfo = JsonUtil.fromJson(doctorInfoJson, DoctorInfo::class.java)
        updateDoctorInfo(doctorInfo, false)
    }

    companion object {
        private const val KEY_TOKEN_INFO: String = "token_info"
        private const val KEY_DOCTOR_INFO: String = "doctor_info"
    }

    fun getToken(): String? {
        return mTokenInfo.value?.token
    }

    fun updateTokenInfo(tokenInfo: TokenInfo?, persist: Boolean = true) {
        mTokenInfo.value = tokenInfo
        if (persist) {
            SPUtils.getInstance().put(KEY_TOKEN_INFO, JsonUtil.toJson(tokenInfo))
        }
    }

    fun updateDoctorInfo(doctorInfo: DoctorInfo?, persist: Boolean = true) {
        mDoctorInfo.value = doctorInfo
        if (persist) {
            SPUtils.getInstance().put(KEY_DOCTOR_INFO, JsonUtil.toJson(doctorInfo))
        }
    }

    fun updateTokenInfoAndDoctorInfo(response: LoginResponse?) {
        updateTokenInfo(response?.getTokenInfo())
        updateDoctorInfo(response?.doctor)
    }

    fun getTokenInfo(): LiveData<TokenInfo> {
        return mTokenInfo
    }

    fun getDoctorInfo(): LiveData<DoctorInfo> {
        return mDoctorInfo
    }

    fun updateTokenInvalidState(tokenIsInvalid: Boolean) {
        mTokenIsInvalid.postValue(tokenIsInvalid)
    }

    fun getLiveDataTokenInvalidState(): LiveData<Boolean> {
        return mTokenIsInvalid
    }

    fun logout() {
        updateTokenInfo(null, true)
    }

    fun isAuthenticated(): Boolean {
        return getAuthenticateStatus() == 2
    }

    fun getAuthenticateStatus(): Int {
        return mDoctorInfo.value?.reviewStatus ?: 0
    }

    fun isVisitorAccount(): Boolean {
        return mDoctorInfo.value?.isVisitorAccount() == true
    }

    fun isLogin(): Boolean {
        return getToken() != null
    }
}