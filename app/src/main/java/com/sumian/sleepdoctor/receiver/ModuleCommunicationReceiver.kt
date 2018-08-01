package com.sumian.sleepdoctor.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.sleepdoctor.account.bean.Token
import com.sumian.sleepdoctor.account.bean.UserInfo
import com.sumian.sleepdoctor.app.AppManager
import com.sumian.sleepdoctor.main.MainActivity
import com.sumian.sleepdoctor.utils.JsonUtil

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/7/30 17:15
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class ModuleCommunicationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null) {
            return
        }
        if (intent.action == "com.sumian.hw.LAUNCH_SLEEP_DOCTOR_MAIN") {
            val tokenInfoString = intent.getStringExtra("token_info")
            if (TextUtils.isEmpty(tokenInfoString)) {
                throw  RuntimeException("token info is null")
            }
            val hwToken = JsonUtil.fromJson<Token>(tokenInfoString, Token::class.java) ?: throw  RuntimeException("token info is null")
            val userInfo = hwToken.user
            val userProfile = UserInfo()
            userProfile.id = userInfo.id.toInt()
            userProfile.mobile = userInfo.mobile
            userProfile.avatar = userInfo.avatar

            val token = Token()
            token.token = hwToken.token
            token.expired_at = hwToken.expired_at
            token.is_new = false

            token.user = userProfile
            AppManager.getAccountViewModel().updateToken(token)
            ActivityUtils.startActivity(MainActivity::class.java)
        }
    }
}