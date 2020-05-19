package com.sumian.sd.common.h5

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.blankj.utilcode.util.ActivityUtils
import com.github.lzyzsd.jsbridge.CallBackFunction
import com.google.gson.reflect.TypeToken
import com.sumian.common.h5.WebViewManger
import com.sumian.common.h5.bean.H5BaseResponse
import com.sumian.common.h5.bean.H5PayloadData
import com.sumian.common.h5.widget.SWebView
import com.sumian.common.helper.ToastHelper
import com.sumian.common.log.CommonLog
import com.sumian.common.utils.JsonUtil
import com.sumian.sd.BuildConfig
import com.sumian.sd.app.AppManager
import com.sumian.sd.base.SdBaseWebViewActivity
import com.sumian.sd.buz.doctor.bean.H5DoctorServiceShoppingResult
import com.sumian.sd.common.pay.activity.PaymentActivity
import com.sumian.sd.main.MainActivity

open class SimpleWebActivity : SdBaseWebViewActivity() {
    private var mTitle: String? = null
    private var mUrlContentPart: String? = null
    private var mUrlComplete: String? = null
    private var mScanQrCodeCallBackFunction: CallBackFunction? = null
    private var mBuyCallBackFunction: CallBackFunction? = null

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
        private const val REQUEST_CODE_SCAN_QR = 1
        private const val REQUEST_CODE_PAY = 104
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
                    .replace("{token}", AppManager.getAccountViewModel().token!!.token)
            val completeUrl = BuildConfig.BASE_H5_URL + urlContent
            CommonLog.log("SimpleWebActivity completeUrl: $completeUrl")
            return getLaunchIntentWithCompleteUrl(context, completeUrl, cls, pageNameForStat)
        }
    }

    override fun registerHandler(sWebView: SWebView) {
        super.registerHandler(sWebView)
        sWebView.registerHandler("buyService") { data, function ->
            mBuyCallBackFunction = function
            val type = object : TypeToken<H5BaseResponse<H5DoctorServiceShoppingResult>>() {}
            val response = JsonUtil.fromJson<H5BaseResponse<H5DoctorServiceShoppingResult>>(data, type.type)
            if (response?.result?.service == null) {
                ToastHelper.show("购买服务包数据错误")
                return@registerHandler
            }
            response?.let {
                PaymentActivity.startForResult(this, it.result!!.service!!, it.result!!.packageId, REQUEST_CODE_PAY)
            }
        }
        sWebView.registerHandler("scanQRCode") { data, function ->
            mScanQrCodeCallBackFunction = function
            startScanQrOrRequestPermission()
        }
    }

    private fun startScanQrOrRequestPermission() {
        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
        var permissionsGranted = true
        for (perm in permissions) {
            permissionsGranted = permissionsGranted && ActivityCompat.checkSelfPermission(this, perm) === PackageManager.PERMISSION_GRANTED
        }
        if (permissionsGranted) {
            startScanQr()
        } else {
            requestQrPermission(permissions)
        }
    }

    private fun startScanQr() {
        startActivityForResult(Intent(this, ScanQrCodeActivity::class.java), ScanQrCodeActivity.RESULT_CODE_SCAN_QR_CODE)
    }

    private fun requestQrPermission(perms: Array<String>) {
        ActivityCompat.requestPermissions(this, perms, REQUEST_CODE_SCAN_QR)
    }

    override fun onGoToPage(page: String, rawData: String) {
        super.onGoToPage(page, rawData)
        if (page == "homeH5") {
            startActivity(MainActivity.getLaunchIntentForH5(WebViewManger.getInstance().getBaseUrl()
                    ?: ""))
            finish()
        }
        if (page == "mine") {
            MainActivity.launch(MainActivity.TAB_2)
            finish()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_SCAN_QR -> {
                var success = true
                for (result in grantResults) {
                    success = success && result === PackageManager.PERMISSION_GRANTED
                }
                if (success) {
                    startScanQr()
                } else {
                    mScanQrCodeCallBackFunction?.onCallBack("{\"error\": \"未获取到扫码所需权限\"}")
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_PAY -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        mBuyCallBackFunction?.onCallBack("{\"result\":\"success\"}")
                    }
                    Activity.RESULT_CANCELED -> {
                        var errMes = data?.getStringExtra(PaymentActivity.EXTRA_ERROR_REASON)
                        if (errMes != null && errMes.isNotEmpty()) {
                            mBuyCallBackFunction?.onCallBack(errMes)
                        }
                    }
                }
                mBuyCallBackFunction = null
            }
            ScanQrCodeActivity.RESULT_CODE_SCAN_QR_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        val code = data?.getStringExtra(ScanQrCodeActivity.EXTRA_RESULT_QR_CODE) ?: ""
                        if (code.isNotEmpty()) {
                            mScanQrCodeCallBackFunction?.onCallBack("{\"content\": \"$code\"}")
                        } else {
                            mScanQrCodeCallBackFunction?.onCallBack("{\"error\": \"获取的二维码为空\"}")
                        }
                    }
                    Activity.RESULT_CANCELED -> {
                        mScanQrCodeCallBackFunction?.onCallBack("{\"error\": \"获取失败\"}")
                    }
                }
                mScanQrCodeCallBackFunction = null
            }
        }
    }
}
