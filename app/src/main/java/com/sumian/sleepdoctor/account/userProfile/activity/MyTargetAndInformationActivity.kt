package com.sumian.sleepdoctor.account.userProfile.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.gson.reflect.TypeToken
import com.sumian.sleepdoctor.account.bean.UserProfile
import com.sumian.sleepdoctor.app.AppManager
import com.sumian.sleepdoctor.base.ActivityLauncher
import com.sumian.sleepdoctor.h5.H5Uri
import com.sumian.sleepdoctor.h5.SimpleWebActivity
import com.sumian.sleepdoctor.h5.bean.H5BaseResponse
import com.sumian.sleepdoctor.utils.JsonUtil
import com.sumian.sleepdoctor.widget.webview.SBridgeHandler
import com.sumian.sleepdoctor.widget.webview.SWebView

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/7/16 17:12
 *     desc   :
 *     version: 1.0
 * </pre>
 */
open class MyTargetAndInformationActivity : SimpleWebActivity() {

    companion object {

        fun launchFromMine(context: Context) {
            launch(context, H5Uri.MY_TARGET_FROM_MINE)
        }

        fun launchFromNewUser(launcher: ActivityLauncher, requestCode: Int) {
            launchForResult(launcher, H5Uri.MY_TARGET_FROM_NEW_USER, requestCode)
        }

        private fun launch(context: Context, urlContentPart: String) {
            val intent = getIntent(context, urlContentPart)
            context.startActivity(intent)
        }

        private fun launchForResult(launcher: ActivityLauncher, urlContentPart: String, requestCode: Int) {
            val intent = getIntent(launcher.activity, urlContentPart)
            launcher.startActivityForResult(intent, requestCode)
        }

        private fun getIntent(context: Context, urlContentPart: String): Intent {
            val bundle = Bundle()
            bundle.putString(KEY_URL_CONTENT_PART, urlContentPart)
            val intent = Intent(context, MyTargetAndInformationActivity::class.java)
            intent.putExtras(bundle)
            return intent
        }
    }

    override fun registerHandler(sWebView: SWebView) {
        super.registerHandler(sWebView)
        sWebView.registerHandler("setPersonalInformation", object : SBridgeHandler() {
            override fun handler(data: String?) {
                val typeToken = object : TypeToken<H5BaseResponse<UserProfile>>() {}
                val response: H5BaseResponse<UserProfile>? = JsonUtil.fromJson(data, typeToken.type)
                if (response != null && response.isSuccess()) {
                    AppManager.getAccountViewModel().updateUserProfile(response.result)
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        })

        sWebView.registerHandler("saveMyTarget", object : SBridgeHandler() {
            override fun handler(data: String?) {
            }
        })
    }
}