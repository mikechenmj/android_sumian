package com.sumian.common.h5

import android.content.Context
import android.webkit.WebResourceResponse
import com.blankj.utilcode.util.LogUtils
import com.sumian.common.h5.widget.SWebView
import java.io.IOException

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/23 11:12
 * desc   :
 * version: 1.0
 */
class H5LocalCacheInterceptor(context: Context, h5FileDir: String) {
    private var mContext: Context = context

    private val H5_ASSET_PATH = "h5/static"
    private val H5_ASSET_JS = "$H5_ASSET_PATH/js"
    private val H5_ASSET_CSS = "$H5_ASSET_PATH/css"
    private val H5_ASSET_IMG = "$H5_ASSET_PATH/img"

    fun interceptH5Request(webView: SWebView) {
        try {
            val assets = mContext.assets
            val jsFiles = assets.list(H5_ASSET_JS)
            val cssFiles = assets.list(H5_ASSET_CSS)
            val imgFiles = assets.list(H5_ASSET_IMG)
            if (jsFiles == null) {
                return
            }
            webView.setWebInterceptor { view, request ->
                val url = request.url.toString()
                var resourceResponse: WebResourceResponse? = null
                val dirPath = getLocalFileDirPath(url) ?: return@setWebInterceptor resourceResponse
                val localFile = getLocalFile(url, jsFiles, cssFiles, imgFiles)
                        ?: return@setWebInterceptor resourceResponse
                val mimeType = getMimeType(url)
                LogUtils.d(localFile)
                try {
                    resourceResponse = WebResourceResponse(mimeType, "UTF-8", mContext.assets.open("$dirPath/$localFile"))
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                resourceResponse
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun getLocalFileDirPath(url: String): String? {
        var dir: String? = null
        if (url.endsWith(".js")) {
            dir = H5_ASSET_JS
        } else if (url.endsWith(".css")) {
            dir = H5_ASSET_CSS
        } else if (url.endsWith(".png") || url.endsWith(".jpg")) {
            dir = H5_ASSET_IMG
        }
        return dir
    }

    private fun getLocalFile(url: String, js: Array<String>?, css: Array<String>?, image: Array<String>?): String? {
        var files: Array<String>? = null
        if (url.endsWith(".js")) {
            files = js
        } else if (url.endsWith(".css")) {
            files = css
        } else if (url.endsWith(".png") || url.endsWith(".jpg")) {
            files = image
        }
        return if (files == null) {
            null
        } else getLocalFile(url, files)
    }

    private fun getLocalFile(url: String, files: Array<String>?): String? {
        if (files == null) {
            return null
        }
        for (file in files) {
            if (url.endsWith(file)) {
                return file
            }
        }
        return null
    }

    /**
     * reference https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Complete_list_of_MIME_types
     *
     * @param url
     * @return
     */
    private fun getMimeType(url: String): String? {
        var mimeType: String? = null
        if (url.endsWith(".js")) {
            mimeType = "application/javascript"
        } else if (url.endsWith(".css")) {
            mimeType = "text/css"
        } else if (url.endsWith(".png")) {
            mimeType = "image/png"
        } else if (url.endsWith(".jpg")) {
            mimeType = "image/jpeg"
        }
        return mimeType
    }
}