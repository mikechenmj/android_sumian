package com.sumian.sd.service.cbti.activity

import android.app.Activity
import android.content.Intent
import com.blankj.utilcode.util.ActivityUtils
import com.google.gson.reflect.TypeToken
import com.sumian.common.h5.bean.H5BaseResponse
import com.sumian.common.h5.bean.H5PayloadData
import com.sumian.common.h5.handler.SBridgeHandler
import com.sumian.common.h5.widget.SWebView
import com.sumian.sd.BuildConfig
import com.sumian.sd.app.AppManager
import com.sumian.sd.base.SdBasePresenter
import com.sumian.sd.base.SdBaseWebViewActivity
import com.sumian.sd.doctor.bean.H5DoctorServiceShoppingResult
import com.sumian.sd.event.CBTIServiceBoughtEvent
import com.sumian.sd.event.EventBusUtil
import com.sumian.sd.h5.H5Uri
import com.sumian.sd.pay.activity.PaymentActivity
import com.sumian.sd.utils.JsonUtil

/**
 * Created by sm
 *
 * on 2018/7/11
 *
 * desc:  CBTI 介绍页，（未购买情况下）
 *
 */
class CBTIIntroductionWebActivity : SdBaseWebViewActivity<SdBasePresenter<*>>() {

    companion object {
        private const val REQUEST_CODE_BUY_SERVICE = 1000
        private const val REQUEST_CODE_BUY_SERVICE_FROM_INTRODUCTION = 1001

        @JvmStatic
        fun show() {
            ActivityUtils.getTopActivity()?.let {
                it.startActivity(Intent(it, CBTIIntroductionWebActivity::class.java))
            }
        }

        @JvmStatic
        fun showForResult() {
            ActivityUtils.getTopActivity()?.let {
                it.startActivityForResult(Intent(it, CBTIIntroductionWebActivity::class.java), REQUEST_CODE_BUY_SERVICE_FROM_INTRODUCTION)
            }
        }
    }

    override fun getCompleteUrl(): String {
        val urlContent = H5Uri.NATIVE_ROUTE.replace("{pageData}", H5PayloadData(H5Uri.CBTI, mapOf()).toJson())
                .replace("{token}", AppManager.getAccountViewModel().token.token)
        return BuildConfig.BASE_H5_URL + urlContent
    }

    override fun registerHandler(sWebView: SWebView) {
        super.registerHandler(sWebView)
        sWebView.registerHandler("buyService", object : SBridgeHandler() {
            override fun handler(data: String?) {
                super.handler(data)
                val type = object : TypeToken<H5BaseResponse<H5DoctorServiceShoppingResult>>() {}
                val response = JsonUtil.fromJson<H5BaseResponse<H5DoctorServiceShoppingResult>>(data, type.type)

                response?.let {
                    PaymentActivity.startForResult(this@CBTIIntroductionWebActivity, it.result?.service!!, it.result!!.packageId, REQUEST_CODE_BUY_SERVICE)
                }
            }
        })
//        sWebView.registerHandler("onCbtiChapterClick", object : SBridgeHandler() {
//            override fun handler(data: String?) {
//                super.handler(data)
//                val type = object : TypeToken<H5BaseResponse<Map<String, Int>>>() {}
//                val response = JsonUtil.fromJson<H5BaseResponse<Map<String, Int>>>(data, type.type)
//                        ?: return
//                val chapterId = response.result?.get("chapterId") ?: return
//                CBTIWeekCoursePartActivity.show(this@CBTIIntroductionWebActivity, chapterId)
//            }
//        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            EventBusUtil.postStickyEvent(CBTIServiceBoughtEvent())
            finish()
        }
    }
}