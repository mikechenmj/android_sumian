package com.sumian.sd.buz.account.model

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.SPUtils
import com.github.lzyzsd.jsbridge.CallBackFunction
import com.sumian.common.buz.kefu.KefuManager
import com.sumian.common.utils.JsonUtil
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.account.bean.Organization
import com.sumian.sd.buz.account.bean.Token
import com.sumian.sd.buz.account.bean.UserInfo
import com.sumian.sd.buz.doctor.bean.Doctor
import com.sumian.sd.common.log.SdLogManager

/**
 * Created by jzz
 * on 2018/1/16.
 * desc:
 */

object AccountManager {
    private val SP_KEY_TOKEN = "token"
    private val SP_KEY_H5_BEARER_TOKEN = "h5_bearer_token"
    private val SP_KEY_USER_INFO = "user_info"
    private val SP_KEY_ORGANIZATION = "organization"
    const val AUTHORIZATION_HEADER = "Authorization"
    const val AUTHORIZATION_HEADER_BEARER_PART = "Bearer "
    private val mTokenLiveData = MutableLiveData<Token>()
    private val mUserInfoLiveData = MutableLiveData<UserInfo>()
    private val mOrganizationLiveData = MutableLiveData<Organization>()
    var h5BearerToken: Token? = null
    private val mH5BearerFunctions: ArrayList<H5BearerFunction?> = ArrayList()
    private val mTokenChangeListeners = ArrayList<TokenChangeListener>()

    val liveDataToken: LiveData<Token>
        get() = mTokenLiveData

    val token: Token?
        get() = mTokenLiveData.value

    val userInfo: UserInfo?
        get() {
            return mUserInfoLiveData.value
        }

    val organization: Organization?
        get() {
            return mOrganizationLiveData.value
        }

    val tokenString: String?
        get() = if (token == null) null else token!!.token

    val isLogin: Boolean
        get() {
            val token = token
            return token != null && !TextUtils.isEmpty(token.getToken())
        }

    val isBindDoctor: Boolean
        get() {
            val userInfo = userInfo
            return userInfo != null && userInfo.isBindDoctor
        }

    init {
        updateToken(loadPersistedToken())
        updateH5BearerToken(loadH5BearerPersistedToken())
        updateUserInfo(loadPersistedUserInfo())
        updateOrganization(loadOrganization())
    }

    private fun loadPersistedToken(): Token? {
        val tokenJson = SPUtils.getInstance().getString(SP_KEY_TOKEN, null)
        return JsonUtil.fromJson(tokenJson, Token::class.java)
    }

    private fun loadH5BearerPersistedToken(): Token? {
        val tokenJson = SPUtils.getInstance().getString(SP_KEY_H5_BEARER_TOKEN, null)
        return JsonUtil.fromJson(tokenJson, Token::class.java)
    }

    private fun persistentToken(token: Token?) {
        SPUtils.getInstance().put(SP_KEY_TOKEN, JsonUtil.toJson(token))
    }

    private fun persistentH5BearerToken(token: Token?) {
        SPUtils.getInstance().put(SP_KEY_H5_BEARER_TOKEN, JsonUtil.toJson(token))
    }

    private fun loadPersistedUserInfo(): UserInfo? {
        val userJson = SPUtils.getInstance().getString(SP_KEY_USER_INFO, null)
        if (userJson == null) { // 老版本 UserInfo 存放在 token 里， 这里做兼容
            return loadPersistedToken()?.user
        }
        return JsonUtil.fromJson(userJson, UserInfo::class.java)
    }

    private fun persistUserInfo(userInfo: UserInfo?) {
        SPUtils.getInstance().put(SP_KEY_USER_INFO, JsonUtil.toJson(userInfo))
    }

    private fun loadOrganization(): Organization? {
        val organizationJson = SPUtils.getInstance().getString(SP_KEY_ORGANIZATION, null)
        return JsonUtil.fromJson(organizationJson, Organization::class.java)
    }

    private fun persistOrganization(organization: Organization?) {
        SPUtils.getInstance().put(SP_KEY_ORGANIZATION, JsonUtil.toJson(organization))
    }

    fun updateBoundDoctor(doctor: Doctor?) {
        val userProfile = userInfo
        if (userProfile != null) {
            if (doctor != null) {
                userProfile.doctor_id = doctor.id
            }
            userProfile.doctor = doctor
            updateUserInfo(userProfile)
        }
    }

    fun registerH5BearerFunction(function: H5BearerFunction?) {
        mH5BearerFunctions.add(function)
    }

    fun unregisterH5BearerFunction(function: H5BearerFunction?) {
        mH5BearerFunctions.remove(function)
    }

    fun updateToken(token: Token?) {
        var isMainLooper = Looper.myLooper() === Looper.getMainLooper()
        if (isMainLooper) {
            mTokenLiveData.value = token
            persistentToken(token)
            onTokenChange(token)
        } else {
            Handler(Looper.getMainLooper()).post {
                mTokenLiveData.value = token
                persistentToken(token)
                onTokenChange(token)
            }
        }
    }

    fun updateH5BearerToken(token: Token?) {
        if (token?.token.isNullOrEmpty()) {
            return
        }
        h5BearerToken = token
        SdLogManager.logToken("updateH5BearerToken: " + token?.token)
        var hasChannelH5Home = false
        for (function in mH5BearerFunctions) {
            hasChannelH5Home = hasChannelH5Home or (function?.isChannelH5Home ?: false)
            function?.callBackFunction?.onCallBack("{\"token\":\"${token?.token}\"}")
        }
        if (hasChannelH5Home) {
            clearAndConsumeH5BearerToken()
        } else {
            persistentH5BearerToken(token)
        }
    }

    fun clearAndConsumeH5BearerToken() {
        h5BearerToken = null
        persistentH5BearerToken(newToken(""))
    }

    @MainThread
    fun updateUserInfo(userInfo: UserInfo?) {
        mUserInfoLiveData.value = userInfo
        persistUserInfo(userInfo)
        if (userInfo != null) {
            KefuManager.setUserInfo(KefuManager.UserInfo(userInfo.id.toString(), userInfo.name, userInfo.avatar, userInfo.mobile))
        }
    }

    @MainThread
    fun updateOrganization(organization: Organization?) {
        mOrganizationLiveData.value = organization
        persistOrganization(organization)
    }

    fun clearToken() {
        updateToken(null)
        updateUserInfo(null)
        updateOrganization(null)
    }

    fun registerTokenChangeListener(listener: TokenChangeListener) {
        if (!mTokenChangeListeners.contains(listener)) {
            mTokenChangeListeners.add(listener)
        }
    }

    fun unregisterTokenChangeListener(listener: TokenChangeListener) {
        mTokenChangeListeners.remove(listener)
    }

    private fun onTokenChange(token: Token?) {
        for (listener in mTokenChangeListeners) {
            listener.onTokenChange(token)
        }
    }

    fun getUserInfoLiveData(): MutableLiveData<UserInfo> {
        return mUserInfoLiveData
    }

    fun getOrganizationLiveData(): MutableLiveData<Organization> {
        return mOrganizationLiveData
    }

    fun newToken(it: String): Token {
        return Token().apply {
            val newTokenString = it.removePrefix(AUTHORIZATION_HEADER_BEARER_PART)
            token = newTokenString
            setExpired_at(0)
            setRefresh_expired_at((System.currentTimeMillis() / 1000L).toInt())
            setUser(AppManager.getAccountViewModel().userInfo)
            is_new = false
        }
    }

    interface TokenChangeListener {
        fun onTokenChange(token: Token?)
    }

    data class H5BearerFunction(val isChannelH5Home: Boolean = false, val callBackFunction: CallBackFunction)
}
