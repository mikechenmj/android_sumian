package com.sumian.sd.common.h5

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import com.github.lzyzsd.jsbridge.CallBackFunction
import com.google.gson.reflect.TypeToken
import com.sumian.common.h5.BaseWebViewFragment
import com.sumian.common.h5.bean.H5BaseResponse
import com.sumian.common.h5.bean.H5BindShareData
import com.sumian.common.h5.widget.SWebView
import com.sumian.common.h5.widget.SWebViewLayout
import com.sumian.common.helper.ToastHelper
import com.sumian.common.statistic.StatUtil
import com.sumian.common.utils.JsonUtil
import com.sumian.sd.BuildConfig
import com.sumian.sd.R
import com.sumian.sd.buz.doctor.bean.H5DoctorServiceShoppingResult
import com.sumian.sd.buz.homepage.sheet.ShareBottomSheet
import com.sumian.sd.buz.stat.StatConstants
import com.sumian.sd.common.pay.activity.PaymentActivity
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import kotlinx.android.synthetic.main.channel_h5_fragment.*

class ChannelH5Fragment : BaseWebViewFragment() {

    var mBuyCallBackFunction: CallBackFunction? = null

    companion object {
        private const val REQUEST_CODE_PAY = 104
    }
    override fun getLayoutId(): Int {
        return R.layout.channel_h5_fragment
    }

    override fun getSWebViewLayout(): SWebViewLayout {
        return sm_webview_container
    }

    override fun getCompleteUrl(): String {
        val token = "?token=" + getToken()
        val completeUrl = BuildConfig.CHANNEL_H5_URL + token
        return completeUrl
    }

    override fun registerHandler(sWebView: SWebView) {
        sWebView.registerHandler("bindShare") { data, function ->
            val shareData = H5BindShareData.fromJson(data)
            getSWebViewLayout().shareView.visibility = View.VISIBLE
            getSWebViewLayout().shareView.setOnClickListener {
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