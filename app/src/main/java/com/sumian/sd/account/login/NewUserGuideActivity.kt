package com.sumian.sd.account.login

import android.app.Activity
import android.view.View
import com.google.gson.reflect.TypeToken
import com.sumian.sd.account.bean.UserInfo
import com.sumian.sd.app.AppManager
import com.sumian.sd.base.SdBasePresenter
import com.sumian.sd.base.SdBaseWebViewActivity
import com.sumian.sd.h5.H5Uri
import com.sumian.sd.h5.bean.H5BaseResponse
import com.sumian.sd.utils.AppUtil
import com.sumian.sd.utils.JsonUtil
import com.sumian.sd.widget.webview.SBridgeHandler
import com.sumian.sd.widget.webview.SWebView

class NewUserGuideActivity : SdBaseWebViewActivity<SdBasePresenter<*>>() {
    override fun initWidget(root: View?) {
        super.initWidget(root)
        mTitleBar.setIsDarkTheme(true)
        mTitleBar.openTopPadding(true)
    }

    override fun getUrlContentPart(): String {
        return H5Uri.NEW_USER_GUIDE
    }

    override fun registerHandler(sWebView: SWebView) {
        super.registerHandler(sWebView)
        sWebView.registerHandler("setPersonalInformation", object : SBridgeHandler() {
            override fun handler(data: String?) {
                val typeToken = object : TypeToken<H5BaseResponse<UserInfo>>() {}
                val response: H5BaseResponse<UserInfo>? = JsonUtil.fromJson(data, typeToken.type)
                if (response != null && response.isSuccess()) {
                    AppManager.getAccountViewModel().updateUserInfo(response.result)
                    AppUtil.launchMainAndFinishAll()
                }
            }
        })

        sWebView.registerHandler("saveMyTarget", object : SBridgeHandler() {
            override fun handler(data: String?) {

            }
        })
    }

    override fun onBackPressed() {
        if (mSWebViewLayout.sWebView.canGoBack()) {
            super.onBackPressed()
        }
    }
}
