package com.sumian.sd.account.userProfile

import android.app.Activity
import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.google.gson.reflect.TypeToken
import com.sumian.common.h5.bean.H5BaseResponse
import com.sumian.common.h5.handler.SBridgeHandler
import com.sumian.common.h5.widget.SWebView
import com.sumian.sd.account.bean.UserInfo
import com.sumian.sd.app.AppManager
import com.sumian.sd.base.SdBasePresenter
import com.sumian.sd.base.SdBaseWebViewActivity
import com.sumian.sd.h5.H5Uri
import com.sumian.common.utils.JsonUtil


/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/7/16 17:12
 *     desc   :
 *     version: 1.0
 * </pre>
 */
open class MyTargetAndInformationActivity : SdBaseWebViewActivity<SdBasePresenter<Any>>() {
    private var mIsFromMine = true

    companion object {
        private const val KEY_IS_FROM_MINE = "IS_FROM_MINE"

        @JvmStatic
        fun launchForResult(activity: Activity, isFromMine: Boolean, requestCode: Int = 0) {
            val bundle = Bundle()
            bundle.putBoolean(KEY_IS_FROM_MINE, isFromMine)
            ActivityUtils.startActivityForResult(bundle, activity, MyTargetAndInformationActivity::class.java, requestCode)
        }
    }

    override fun getUrlContentPart(): String {
        mIsFromMine = intent.getBooleanExtra(KEY_IS_FROM_MINE, true)
        return if (mIsFromMine) H5Uri.MY_TARGET_FROM_MINE else H5Uri.MY_TARGET_FROM_NEW_USER
    }

    override fun registerHandler(sWebView: SWebView) {
        super.registerHandler(sWebView)
        sWebView.registerHandler("setPersonalInformation", object : SBridgeHandler() {
            override fun handler(data: String?) {
                val typeToken = object : TypeToken<H5BaseResponse<UserInfo>>() {}
                val response: H5BaseResponse<UserInfo>? = JsonUtil.fromJson(data, typeToken.type)
                if (response != null && response.isSuccess()) {
                    AppManager.getAccountViewModel().updateUserInfo(response.result)
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        })

        sWebView.registerHandler("saveMyTarget", object : SBridgeHandler() {
            override fun handler(data: String?) {
                if (mIsFromMine) {
                    finish()
                }
            }
        })
    }
}