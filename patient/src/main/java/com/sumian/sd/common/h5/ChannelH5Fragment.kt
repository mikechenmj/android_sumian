package com.sumian.sd.common.h5

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.util.TypedValue
import android.view.View
import androidx.core.app.ActivityCompat
import com.github.lzyzsd.jsbridge.CallBackFunction
import com.google.gson.reflect.TypeToken
import com.sumian.common.h5.BaseWebViewFragment
import com.sumian.common.h5.WebViewManger
import com.sumian.common.h5.bean.H5BaseResponse
import com.sumian.common.h5.bean.H5BindShareData
import com.sumian.common.h5.widget.SWebView
import com.sumian.common.helper.ToastHelper
import com.sumian.common.statistic.StatUtil
import com.sumian.common.utils.JsonUtil
import com.sumian.sd.BuildConfig
import com.sumian.sd.buz.account.login.LoginActivity
import com.sumian.sd.buz.doctor.bean.H5DoctorServiceShoppingResult
import com.sumian.sd.buz.homepage.sheet.ShareBottomSheet
import com.sumian.sd.buz.stat.StatConstants
import com.sumian.sd.common.h5.ScanQrCodeActivity.EXTRA_RESULT_QR_CODE
import com.sumian.sd.common.h5.ScanQrCodeActivity.RESULT_CODE_SCAN_QR_CODE
import com.sumian.sd.common.h5.bean.ShowNavTab
import com.sumian.sd.common.pay.activity.PaymentActivity
import com.sumian.sd.common.utils.EventBusUtil
import com.sumian.sd.main.MainActivity
import com.sumian.sd.main.MainActivity.Companion.TAB_1
import com.sumian.sd.main.MainActivity.Companion.TAB_2
import com.sumian.sd.main.event.ChangeMainTabEvent
import com.tencent.smtt.sdk.WebView
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA

class ChannelH5Fragment : BaseWebViewFragment() {

    private var mBuyCallBackFunction: CallBackFunction? = null
    private var mScanQrCodeCallBackFunction: CallBackFunction? = null

    companion object {
        private const val REQUEST_CODE_PAY = 104
        private const val REQUEST_CODE_SCAN_QR = 1
    }

    override fun initWidget() {
        super.initWidget()
        mTitleBar?.layoutParams?.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 65f, resources.displayMetrics).toInt()
        mTitleBar?.setPadding(0, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics).toInt(), 0, 0)
    }

    override fun onProgressChange(view: WebView?, newProgress: Int) {
        super.onProgressChange(view, newProgress)
        var parent = activity ?: return
        if (parent is MainActivity) {
        } else {
            return
        }
        parent.setNavTabVisible(isCurrentH5HomeUrl())
    }

    override fun onPageFinish(view: WebView?) {
        super.onPageFinish(view)
        if (activity != null) {
            var act = activity as MainActivity
            var targetUrl = act.targetUrl
            if (!isH5HomeUrl(targetUrl) && getSWebViewLayout()?.sWebView?.url != targetUrl) {
                Handler().postDelayed({ getSWebViewLayout()?.sWebView?.loadRequestUrl(targetUrl) }, 1000)
            }
            act.targetUrl = null
        }
    }

    override fun onGoToPage(page: String, rawData: String) {
        super.onGoToPage(page, rawData)
        if (page == "deviceDatas") {
            EventBusUtil.postStickyEvent(ChangeMainTabEvent(TAB_1))
        }
        if (page == "mine") {
            EventBusUtil.postStickyEvent(ChangeMainTabEvent(TAB_2))
        }
    }

    private fun isH5HomeUrl(url: String?): Boolean {
        if (url === null) {
            return true
        }
        if (url == WebViewManger.getInstance().getBaseUrl() + "ks-index") {
            return true
        }
        if (url == WebViewManger.getInstance().getBaseUrl() + "home") {
            return true
        }
        if (url == WebViewManger.getInstance().getBaseUrl()) {
            return true
        }
        var index = url.indexOfFirst { c ->
            c.toString() == "?"
        }
        if (index <= 0) {
            return false
        }
        var baseUrl = url.substring(0, index)
        return baseUrl == WebViewManger.getInstance().getBaseUrl()
    }

    fun isCurrentH5HomeUrl(): Boolean {
        var url = getSWebViewLayout()?.sWebView?.url
        return isH5HomeUrl(url)

    }

    override fun registerHandler(sWebView: SWebView) {
        sWebView.registerHandler("bindShare") { data, function ->
            val shareData = H5BindShareData.fromJson(data)
            if (shareData.platform.size <= 0) {
                mShare?.visibility = View.INVISIBLE
                return@registerHandler
            }
            mShare?.visibility = View.VISIBLE
            mShare?.setOnClickListener {
                if (shareData.weixin == null || shareData.weixinCircle == null) {
                    return@setOnClickListener
                }
                ShareBottomSheet.show(childFragmentManager, shareData.weixin!!, shareData.weixinCircle!!,
                        object : UMShareListener {
                            override fun onResult(p0: SHARE_MEDIA?) {

                            }

                            override fun onCancel(p0: SHARE_MEDIA?) {
                            }

                            override fun onError(p0: SHARE_MEDIA?, p1: Throwable?) {
                            }

                            override fun onStart(p0: SHARE_MEDIA?) {
                                StatUtil.event(StatConstants.on_relaxation_detail_page_share_success)
                            }
                        })
            }
        }
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
        sWebView.registerHandler("tokenInvalid") { data, function ->
            LoginActivity.show()
        }
        sWebView.registerHandler("scanQRCode") { data, function ->
            mScanQrCodeCallBackFunction = function
            startScanQrOrRequestPermission()
        }
        sWebView.registerHandler("showNavTab") { data, function ->
            val act = activity ?: return@registerHandler
            if (act is MainActivity) {
            } else {
                return@registerHandler
            }
            val type = object : TypeToken<ShowNavTab>() {}
            val response = JsonUtil.fromJson<ShowNavTab>(data, type.type) ?: return@registerHandler
            act.setNavTabVisible(response.showNavTab)
        }
    }

    private fun startScanQrOrRequestPermission() {
        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
        var permissionsGranted = true
        for (perm in permissions) {
            permissionsGranted = permissionsGranted && ActivityCompat.checkSelfPermission(activity!!, perm) === PackageManager.PERMISSION_GRANTED
        }
        if (permissionsGranted) {
            startScanQr()
        } else {
            requestQrPermission(permissions)
        }
    }

    private fun requestQrPermission(perms: Array<String>) {
        requestPermissions(perms, REQUEST_CODE_SCAN_QR)
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

    private fun startScanQr() {
        startActivityForResult(Intent(activity, ScanQrCodeActivity::class.java), RESULT_CODE_SCAN_QR_CODE)
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
            RESULT_CODE_SCAN_QR_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        val code = data?.getStringExtra(EXTRA_RESULT_QR_CODE) ?: ""
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