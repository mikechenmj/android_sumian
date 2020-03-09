package com.sumian.sd.common.h5

import android.app.Activity
import android.content.Intent
import android.view.View
import com.github.lzyzsd.jsbridge.CallBackFunction
import com.google.gson.reflect.TypeToken
import com.sumian.common.h5.BaseWebViewFragment
import com.sumian.common.h5.bean.H5BaseResponse
import com.sumian.common.h5.bean.H5BindShareData
import com.sumian.common.h5.widget.SWebView
import com.sumian.common.helper.ToastHelper
import com.sumian.common.statistic.StatUtil
import com.sumian.common.utils.JsonUtil
import com.sumian.sd.BuildConfig
import com.sumian.sd.buz.doctor.bean.H5DoctorServiceShoppingResult
import com.sumian.sd.buz.homepage.sheet.ShareBottomSheet
import com.sumian.sd.buz.stat.StatConstants
import com.sumian.sd.common.pay.activity.PaymentActivity
import com.sumian.sd.main.MainActivity
import com.tencent.smtt.sdk.WebView
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA

class ChannelH5Fragment : BaseWebViewFragment() {

    private var mBuyCallBackFunction: CallBackFunction? = null

    companion object {
        private const val REQUEST_CODE_PAY = 104
    }

    override fun onProgressChange(view: WebView?, newProgress: Int) {
        super.onProgressChange(view, newProgress)
        var parent = activity ?: return
        if (parent is MainActivity) {
        } else {
            return
        }
        var url = view?.url ?: return

        if (url == "https://ch-test.sumian.com/ks-index") {
            parent.setNavTabVisible(true)
            return
        }
        var index = url.indexOfFirst { c ->
            c.toString() == "?"
        }
        if (index <= 0) {
            parent.setNavTabVisible(false)
            return
        }
        var baseUrl = url.substring(0, index)
        if (baseUrl != BuildConfig.CHANNEL_H5_URL) {
            parent.setNavTabVisible(false)
        } else {
            parent.setNavTabVisible(true)
        }
    }

    override fun getCompleteUrl(): String {
        val token = "?token=" + getToken()
        return BuildConfig.CHANNEL_H5_URL + token
    }


    override fun registerHandler(sWebView: SWebView) {
        sWebView.registerHandler("bindShare") { data, function ->
            val shareData = H5BindShareData.fromJson(data)
            if (shareData.platform.size <= 0) {
                mShare?.visibility = View.GONE
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PAY) {
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
        }
    }
}