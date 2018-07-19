package com.sumian.sleepdoctor.homepage

import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.github.lzyzsd.jsbridge.CallBackFunction
import com.google.gson.reflect.TypeToken
import com.sumian.sleepdoctor.base.BasePresenter
import com.sumian.sleepdoctor.base.BaseWebViewActivity
import com.sumian.sleepdoctor.event.EventBusUtil
import com.sumian.sleepdoctor.event.SleepPrescriptionUpdatedEvent
import com.sumian.sleepdoctor.h5.H5Uri
import com.sumian.sleepdoctor.h5.bean.H5BaseResponse
import com.sumian.sleepdoctor.homepage.bean.SleepPrescriptionWrapper
import com.sumian.sleepdoctor.utils.JsonUtil
import com.sumian.sleepdoctor.widget.webview.SBridgeHandler
import com.sumian.sleepdoctor.widget.webview.SWebView

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/7/18 14:24
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class SleepPrescriptionSettingActivity : BaseWebViewActivity<BasePresenter<*>>() {

    companion object {
        private const val KEY_DATA = "data"

        fun launch(sleepPrescriptionWrapper: SleepPrescriptionWrapper) {
            val bundle = Bundle()
            bundle.putString(KEY_DATA, JsonUtil.toJson(sleepPrescriptionWrapper))
            ActivityUtils.startActivity(bundle, SleepPrescriptionSettingActivity::class.java)
        }
    }

    override fun getUrlContentPart(): String {
        return H5Uri.SLEEP_PRESCRIPTION.replace("{data}", intent.getStringExtra(KEY_DATA))
    }

    override fun registerHandler(sWebView: SWebView) {
        super.registerHandler(sWebView)
        sWebView.registerHandler("sleepPrescriptionUpdated", object : SBridgeHandler() {
            override fun handler(data: String?, function: CallBackFunction?) {
                LogUtils.d(data)
                val type = object : TypeToken<H5BaseResponse<SleepPrescriptionWrapper>>() {}.type
                val response = JsonUtil.fromJson<H5BaseResponse<SleepPrescriptionWrapper>>(data, type)
                        ?: return
                EventBusUtil.postSticky(SleepPrescriptionUpdatedEvent(response.result ?: return))
                finish()
            }
        })
    }
}