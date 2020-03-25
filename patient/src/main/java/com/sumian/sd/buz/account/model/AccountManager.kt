package com.sumian.sd.buz.account.model

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.SPUtils
import com.sumian.common.buz.kefu.KefuManager
import com.sumian.common.utils.JsonUtil
import com.sumian.sd.buz.account.bean.Token
import com.sumian.sd.buz.account.bean.UserInfo
import com.sumian.sd.buz.doctor.bean.Doctor

/**
 * Created by jzz
 * on 2018/1/16.
 * desc:
 */

object AccountManager {
    private val SP_KEY_TOKEN = "token"
    private val SP_KEY_USER_INFO = "user_info"
    private val mTokenLiveData = MutableLiveData<Token>()
    private val mUserInfoLiveData = MutableLiveData<UserInfo>()
    private val mTokenChangeListeners = ArrayList<TokenChangeListener>()

    val liveDataToken: LiveData<Token>
        get() = mTokenLiveData

    val token: Token?
        get() = mTokenLiveData.value

    val userInfo: UserInfo?
        get() {
            return mUserInfoLiveData.value
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
        updateUserInfo(loadPersistedUserInfo())
    }

    private fun loadPersistedToken(): Token? {
        val tokenJson = SPUtils.getInstance().getString(SP_KEY_TOKEN, null)
        return JsonUtil.fromJson(tokenJson, Token::class.java)
    }

    private fun persistentToken(token: Token?) {
        SPUtils.getInstance().put(SP_KEY_TOKEN, JsonUtil.toJson(token))
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

    @MainThread
    fun updateUserInfo(userInfo: UserInfo?) {
        mUserInfoLiveData.value = userInfo
        persistUserInfo(userInfo)
        if (userInfo != null) {
            KefuManager.setUserInfo(KefuManager.UserInfo(userInfo.id.toString(), userInfo.name, userInfo.avatar, userInfo.mobile))
        }
    }

    fun clearToken() {
        updateToken(null)
        updateUserInfo(null)
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

    interface TokenChangeListener {
        fun onTokenChange(token: Token?)
    }
}
