package com.sumian.sd.base

import com.google.gson.reflect.TypeToken
import com.sumian.common.h5.BaseWebViewActivity
import com.sumian.common.h5.bean.H5BaseResponse
import com.sumian.common.h5.bean.H5BearerToken
import com.sumian.common.h5.widget.SWebView
import com.sumian.common.utils.JsonUtil
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.account.model.AccountManager
import com.sumian.sd.common.log.SdLogManager

/**
 * Created by sm
 * on 2018/5/25 10:03
 * desc:
 */
abstract class SdBaseWebViewActivity : BaseWebViewActivity() {

    private var mUpdateH5BearerFunction: AccountManager.H5BearerFunction? = null

    override fun registerHandler(sWebView: SWebView) {
        super.registerHandler(sWebView)
        sWebView.registerHandler("updateH5BearerToken") { data, function ->
            val type = object : TypeToken<H5BaseResponse<H5BearerToken>>() {}
            val result = JsonUtil.fromJson<H5BaseResponse<H5BearerToken>>(data, type.type)?.result
            if (result != null && !result.token.isNullOrEmpty()) {
                SdLogManager.logToken("$localClassName H5 通知 App 更新 token: " + result.token)
                AppManager.getAccountViewModel().updateToken(AccountManager.newToken(result.token)) // H5 通知 App 更新 token
            } else if (AppManager.getAccountViewModel().h5BearerToken != null) {
                SdLogManager.logToken("$localClassName H5 发现 App 中有需要更新的 token: " + AppManager.getAccountViewModel().h5BearerToken?.token)
                function.onCallBack("{\"token\":\"${AppManager.getAccountViewModel().h5BearerToken?.token}\"}") // H5 发现 App 中有需要更新的 token
                AppManager.getAccountViewModel().clearAndConsumeH5BearerToken()
            } else {
                SdLogManager.logToken("$localClassName 监听 App token 更新")
                if (mUpdateH5BearerFunction != null) {
                    AppManager.getAccountViewModel().unregisterH5BearerFunction(mUpdateH5BearerFunction)
                }
                mUpdateH5BearerFunction = AccountManager.H5BearerFunction(true, function)
                AppManager.getAccountViewModel().registerH5BearerFunction(mUpdateH5BearerFunction) // 监听 App token 更新
            }
        }
    }

    override fun onDestroy() {
        if (mUpdateH5BearerFunction != null) {
            AppManager.getAccountViewModel().unregisterH5BearerFunction(mUpdateH5BearerFunction)
            mUpdateH5BearerFunction = null
        }
        super.onDestroy()
    }
}
