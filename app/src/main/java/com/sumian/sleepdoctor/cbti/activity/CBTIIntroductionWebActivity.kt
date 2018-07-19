package com.sumian.sleepdoctor.cbti.activity

import android.app.Activity
import android.content.Intent
import com.sumian.sleepdoctor.base.BasePresenter
import com.sumian.sleepdoctor.base.BaseWebViewActivity
import com.sumian.sleepdoctor.doctor.activity.PaymentActivity
import com.sumian.sleepdoctor.doctor.bean.DoctorServiceShopData
import com.sumian.sleepdoctor.event.CbtiServiceBoughtEvent
import com.sumian.sleepdoctor.event.EventBusUtil
import com.sumian.sleepdoctor.h5.H5Uri
import com.sumian.sleepdoctor.utils.JsonUtil
import com.sumian.sleepdoctor.widget.webview.SBridgeHandler
import com.sumian.sleepdoctor.widget.webview.SWebView

/**
 * Created by sm
 *
 * on 2018/7/11
 *
 * desc:
 *
 */
class CBTIIntroductionWebActivity : BaseWebViewActivity<BasePresenter<*>>() {

    companion object {
        private const val REQUEST_CODE_BUY_SERVICE = 1000
    }

    override fun initTitle(): String {
        return "CBTI详细介绍, web页面"
    }

    override fun getUrlContentPart(): String? {
        return H5Uri.CBTI_INTRODUCTION
    }

    override fun registerHandler(sWebView: SWebView) {
        super.registerHandler(sWebView)
        sWebView.registerHandler("buyService", object : SBridgeHandler() {
            override fun handler(data: String?) {
                super.handler(data)
                val doctorService = JsonUtil.fromJson<DoctorServiceShopData>(data, DoctorServiceShopData::class.java)
                PaymentActivity.startForResult(this@CBTIIntroductionWebActivity, doctorService, REQUEST_CODE_BUY_SERVICE)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            EventBusUtil.postSticky(CbtiServiceBoughtEvent())
            reload()
        }
    }
}