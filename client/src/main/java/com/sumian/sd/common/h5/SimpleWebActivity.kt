package com.sumian.sd.common.h5

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.h5.bean.H5PayloadData
import com.sumian.sd.BuildConfig
import com.sumian.sd.app.AppManager
import com.sumian.sd.base.SdBaseWebViewActivity

open class SimpleWebActivity : SdBaseWebViewActivity() {
    private var mTitle: String? = null
    private var mUrlContentPart: String? = null
    private var mUrlComplete: String? = null

    override fun initBundle(bundle: Bundle) {
        mTitle = bundle.getString(KEY_TITLE)
        mUrlContentPart = bundle.getString(KEY_URL_CONTENT_PART)
        mUrlComplete = bundle.getString(KEY_URL_COMPLETE)
    }

    override fun initTitle(): String? {
        return mTitle
    }

    override fun getUrlContentPart(): String? {
        return mUrlContentPart
    }

    override fun getCompleteUrl(): String {
        return if (mUrlComplete != null) {
            mUrlComplete!!
        } else super.getCompleteUrl()
    }

    override fun getPageName(): String {
        return intent.getStringExtra(KEY_PAGE_NAME) ?: super.getPageName()
    }

    companion object {

        val KEY_TITLE = "KEY_TITLE"
        val KEY_URL_CONTENT_PART = "KEY_URL_CONTENT_PART"
        val KEY_URL_COMPLETE = "KEY_URL_COMPLETE"
        val KEY_PAGE_NAME = "KEY_PAGE_NAME"

        @JvmOverloads
        fun launch(context: Context, urlContentPart: String, pageNameForStat: String? = null) {
            val intent = getLaunchIntentWithPartUrl(context, urlContentPart, pageNameForStat)
            ActivityUtils.startActivity(intent)
        }

        @JvmOverloads
        private fun getLaunchIntentWithPartUrl(context: Context, urlContentPart: String, pageNameForStat: String? = null): Intent {
            val intent = Intent(context, SimpleWebActivity::class.java)
            intent.putExtra(KEY_URL_CONTENT_PART, urlContentPart)
            intent.putExtra(KEY_PAGE_NAME, pageNameForStat)
            return intent
        }

        private fun getLaunchIntentWithCompleteUrl(context: Context, completeUrl: String, cls: Class<out SimpleWebActivity>, pageNameForStat: String? = null): Intent {
            val intent = Intent(context, cls)
            intent.putExtra(KEY_URL_COMPLETE, completeUrl)
            intent.putExtra(KEY_PAGE_NAME, pageNameForStat)
            return intent
        }

        fun getLaunchIntentWithRouteData(context: Context, pageName: String, data: Map<String, Any?>? = null, pageNameForStat: String? = null): Intent {
            return getLaunchIntentWithRouteData(context, H5PayloadData(pageName, data).toJson(), SimpleWebActivity::class.java)
        }

        @JvmOverloads
        fun getLaunchIntentWithRouteData(context: Context, routePageData: String, cls: Class<out SimpleWebActivity> = SimpleWebActivity::class.java, pageNameForStat: String? = null): Intent {
            val urlContent = H5Uri.NATIVE_ROUTE
                    .replace("{pageData}", routePageData)
                    .replace("{token}", AppManager.getAccountViewModel().token.token)
            val completeUrl = BuildConfig.BASE_H5_URL + urlContent
            return getLaunchIntentWithCompleteUrl(context, completeUrl, cls, pageNameForStat)
        }
    }
}
