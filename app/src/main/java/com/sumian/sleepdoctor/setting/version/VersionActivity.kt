package com.sumian.sleepdoctor.setting.version

import android.net.Uri
import android.util.Log
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.github.lzyzsd.jsbridge.CallBackFunction
import com.sumian.common.media.SelectImageActivity
import com.sumian.common.media.config.SelectOptions
import com.sumian.sleepdoctor.base.BasePresenter
import com.sumian.sleepdoctor.base.BaseWebViewActivity
import com.sumian.sleepdoctor.h5.bean.ImageCount
import com.sumian.sleepdoctor.utils.JsonUtil
import com.sumian.sleepdoctor.widget.webview.SBridgeHandler
import com.sumian.sleepdoctor.widget.webview.SWebView

/**
 * Created by sm
 *
 * on 2018/7/6
 *
 * desc:
 *
 */
class VersionActivity : BaseWebViewActivity<BasePresenter<Any>>(), SWebView.OnRequestFileCallback {


    private val TAG = VersionActivity::class.java.simpleName

    override fun getUrlContentPart(): String? {
        return "toast-demo"
    }

    override fun h5HandlerName(): String? {
        return "getImgUrl"
    }

    override fun registerHandler(sWebView: SWebView) {
        super.registerHandler(sWebView)
        sWebView.setOnRequestFileCallback(this)
        sWebView.registerHandler(h5HandlerName(), object : SBridgeHandler() {

            override fun handler(data: String, function: CallBackFunction) {
                //super.handler(data, function);
                val imageCount = JsonUtil.fromJson(data, ImageCount::class.java) ?: return

                Log.e(TAG, "handler: ----1---->" + imageCount.toString())

                SelectImageActivity.show(this@VersionActivity, SelectOptions.Builder()
                        .setHasCam(true)
                        .setCallback {
                            Log.e(TAG, "handler: ------>" + it.toString())
                        }.setSelectCount(imageCount.selectQuantity).setSelectedImages(arrayListOf()).build())
            }
        })
    }

    override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: WebChromeClient.FileChooserParams?): Boolean {
        SelectImageActivity.show(this@VersionActivity, SelectOptions.Builder()
                .setHasCam(true)
                .setCallback {
                    val uris = arrayOfNulls<Uri>(it.size)

                    it.forEachIndexed { index, imagesPath ->
                        run {
                            uris[index] = Uri.parse(imagesPath)
                        }
                    }

                    filePathCallback?.onReceiveValue(uris.requireNoNulls())
                    Log.e(TAG, "handler: ------>" + uris.toString())
                }.setSelectCount(9).setSelectedImages(arrayListOf()).build())
        return true
    }
}
