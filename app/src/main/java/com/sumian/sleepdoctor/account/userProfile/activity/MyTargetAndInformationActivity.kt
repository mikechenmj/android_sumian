package com.sumian.sleepdoctor.account.userProfile.activity

import android.app.Activity
import android.content.Context
import com.sumian.sleepdoctor.base.ActivityLauncher
import com.sumian.sleepdoctor.h5.H5Uri
import com.sumian.sleepdoctor.h5.SimpleWebActivity
import com.sumian.sleepdoctor.h5.bean.H5BaseResponse
import com.sumian.sleepdoctor.utils.JsonUtil
import com.sumian.sleepdoctor.widget.webview.SBridgeHandler
import com.sumian.sleepdoctor.widget.webview.SWebView
import com.umeng.socialize.utils.DeviceConfig.context

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/7/16 17:12
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class MyTargetAndInformationActivity : SimpleWebActivity() {

    companion object {
        fun launch(context: Context, fromMine: Boolean) {
            launch(context, if (fromMine) H5Uri.MY_TARGET_FROM_MINE else H5Uri.MY_TARGET_FROM_NEW_USER)
        }

        fun launchFromMine() {
            launch(context, H5Uri.MY_TARGET_FROM_MINE)
        }

        fun launchFromNewUser(launcher: ActivityLauncher, requestCode: Int) {
            launchForResult(launcher, H5Uri.MY_TARGET_FROM_NEW_USER, requestCode)
        }
    }

    override fun registerHandler(sWebView: SWebView?) {
        super.registerHandler(sWebView)
        sWebView?.registerHandler("setPersonalInformation", object : SBridgeHandler() {
            override fun handler(data: String?) {
                val response = JsonUtil.fromJson(data, H5BaseResponse::class.java)
                if (response != null && response.isSuccess()) {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        })
    }
}