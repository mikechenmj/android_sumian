package com.sumian.sd.buz.homepage

import android.app.Activity
import android.content.Intent
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.github.lzyzsd.jsbridge.CallBackFunction
import com.google.gson.reflect.TypeToken
import com.sumian.common.h5.bean.H5BaseResponse
import com.sumian.common.h5.bean.NativeRouteData
import com.sumian.common.h5.handler.SBridgeHandler
import com.sumian.common.h5.widget.SWebView
import com.sumian.common.utils.JsonUtil
import com.sumian.sd.R
import com.sumian.sd.base.SdBaseWebViewActivity
import com.sumian.sd.buz.diary.fillsleepdiary.FillSleepDiaryActivity
import com.sumian.sd.buz.homepage.bean.SleepPrescriptionWrapper
import com.sumian.sd.buz.homepage.event.SleepPrescriptionUpdatedEvent
import com.sumian.sd.buz.stat.StatConstants
import com.sumian.sd.common.h5.H5Uri
import com.sumian.sd.common.utils.EventBusUtil
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
class SleepPrescriptionActivity : SdBaseWebViewActivity() {

    companion object {
        private const val REQUEST_CODE_FILL_DIARY = 1000
        fun launch() {
            ActivityUtils.startActivity(SleepPrescriptionActivity::class.java)
        }
    }

//    override fun getPageName(): String {
//        return StatConstants.page_sleep_prescription_edit
//    }

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
                val response = JsonUtil.fromJson<H5BaseResponse<SleepPrescriptionWrapper>>(data, type)
                        ?: return
                EventBusUtil.postStickyEvent(SleepPrescriptionUpdatedEvent(response.result
                        ?: return))
                reload()
            }
        })
    }

    override fun onGoToPage(page: String, rawData: String) {
        when (page) {
            "sleepDiarySubmit" -> {
                val date = JsonUtil.fromJson<NativeRouteData<DateBean>>(rawData, object : TypeToken<NativeRouteData<DateBean>>() {}.type)
                        ?: return
                FillSleepDiaryActivity.startForResult(this@SleepPrescriptionActivity, date.data?.date!! * 1000L, REQUEST_CODE_FILL_DIARY)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_FILL_DIARY) {
            if (resultCode == Activity.RESULT_OK) {
                reload()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    data class DateBean(val date: Int)
}