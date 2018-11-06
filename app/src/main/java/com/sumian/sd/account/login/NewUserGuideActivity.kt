package com.sumian.sd.account.login

import android.graphics.Color
import android.webkit.WebView
import com.google.gson.reflect.TypeToken
import com.sumian.common.h5.bean.H5BaseResponse
import com.sumian.common.h5.handler.SBridgeHandler
import com.sumian.common.h5.widget.SWebView
import com.sumian.common.utils.ColorCompatUtil
import com.sumian.common.widget.TitleBar
import com.sumian.sd.R
import com.sumian.sd.account.bean.UserInfo
import com.sumian.sd.app.AppManager
import com.sumian.sd.base.SdBasePresenter
import com.sumian.sd.base.SdBaseWebViewActivity
import com.sumian.sd.h5.H5Uri
import com.sumian.sd.utils.AppUtil
import com.sumian.sd.utils.JsonUtil
import com.sumian.sd.utils.StatusBarUtil

class NewUserGuideActivity : SdBaseWebViewActivity<SdBasePresenter<*>>() {
    private val mTitleBar: TitleBar by lazy {
        getTitleBar()
    }

    override fun initWidget() {
        super.initWidget()
        StatusBarUtil.setStatusBarTextColorDark(this, true)
        mTitleBar.openTopPadding(true)
        mTitleBar.setBackgroundColor(Color.WHITE)
        mTitleBar.showTitle(false)
        mTitleBar.showBackArrow(false)
        mTitleBar.mIvBack.setColorFilter(ColorCompatUtil.getColor(this, R.color.colorPrimary))
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

    override fun monitorKeyboard() {
    }

    override fun onBackPressed() {
        if (canWebGoBack()) {
            super.onBackPressed()
        }
    }

    override fun onPageFinish(view: WebView?) {
        super.onPageFinish(view)
        mTitleBar.showBackArrow((canWebGoBack()))
    }
}
