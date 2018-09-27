package com.sumian.sd.homepage

import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.github.lzyzsd.jsbridge.CallBackFunction
import com.google.gson.reflect.TypeToken
import com.sumian.common.h5.bean.H5BaseResponse
import com.sumian.common.h5.handler.SBridgeHandler
import com.sumian.common.h5.widget.SWebView
import com.sumian.sd.R
import com.sumian.sd.base.SdBasePresenter
import com.sumian.sd.base.SdBaseWebViewActivity
import com.sumian.sd.event.EventBusUtil
import com.sumian.sd.event.SleepPrescriptionUpdatedEvent
import com.sumian.sd.h5.H5Uri
import com.sumian.sd.homepage.bean.SleepPrescriptionWrapper
import com.sumian.sd.utils.JsonUtil
import com.sumian.sd.widget.dialog.SumianTitleMessageDialog

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/7/18 14:24
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class SleepPrescriptionSettingActivity : SdBaseWebViewActivity<SdBasePresenter<*>>() {

    companion object {

        fun launch() {
            ActivityUtils.startActivity(SleepPrescriptionSettingActivity::class.java)
        }
    }

    override fun initWidget() {
        super.initWidget()
        getTitleBar().showMoreIcon(R.drawable.ic_nav_icon_sleep_prescription_details)
        getTitleBar().setOnMenuClickListener {
            SumianTitleMessageDialog(this)
                    .setTitle(resources.getString(R.string.sleep_prescription_introduction_title))
                    .setMessage(resources.getString(R.string.sleep_prescription_introduction_content))
                    .show()
        }
    }

    override fun getUrlContentPart(): String {
        return H5Uri.SLEEP_PRESCRIPTION
    }

    override fun registerHandler(sWebView: SWebView) {
        super.registerHandler(sWebView)
        sWebView.registerHandler("sleepPrescriptionUpdated", object : SBridgeHandler() {
            override fun handler(data: String?, function: CallBackFunction?) {
                LogUtils.d(data)
                val type = object : TypeToken<H5BaseResponse<SleepPrescriptionWrapper>>() {}.type
                val response = JsonUtil.fromJson<H5BaseResponse<SleepPrescriptionWrapper>>(data, type) ?: return
                EventBusUtil.postStickyEvent(SleepPrescriptionUpdatedEvent(response.result ?: return))
                finish()
            }
        })
    }
}