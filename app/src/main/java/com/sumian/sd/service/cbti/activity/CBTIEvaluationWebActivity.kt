package com.sumian.sd.service.cbti.activity

import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.google.gson.reflect.TypeToken
import com.sumian.common.h5.bean.H5BaseResponse
import com.sumian.common.h5.handler.SBridgeHandler
import com.sumian.common.h5.widget.SWebView
import com.sumian.sd.base.SdBasePresenter
import com.sumian.sd.base.SdBaseWebViewActivity
import com.sumian.sd.h5.H5Uri
import com.sumian.sd.service.cbti.bean.CBTIEvaluationH5Data
import com.sumian.sd.utils.JsonUtil

/**
 * Created by sm
 *
 * on 2018/7/11
 *
 * desc:CBTI  前、中期评估（量表）
 *
 */
class CBTIEvaluationWebActivity : SdBaseWebViewActivity<SdBasePresenter<*>>() {

    companion object {
        private const val EXTRAS_SCALE_IDS = "com.sumian.sd.extra.scale.ids"
        private const val EXTRAS_CHAPTER_ID = "com.sumian,sd.extra.chapter.id"

        fun show(evaluations: String, chapterId: Int) {
            ActivityUtils.getTopActivity()?.let {
                it.startActivity(Intent(it, CBTIEvaluationWebActivity::class.java).apply {
                    putExtra(EXTRAS_SCALE_IDS, evaluations)
                    putExtra(EXTRAS_CHAPTER_ID, chapterId)
                })
            }
        }
    }

    private lateinit var scaleIds: String
    private var chapterId: Int = 0

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        this.scaleIds = bundle.getString(EXTRAS_SCALE_IDS, "")
        this.chapterId = bundle.getInt(EXTRAS_CHAPTER_ID, 0)
    }

    override fun initWidget() {
        super.initWidget()
        getTitleBar().openTopPadding(true)
    }

    override fun getUrlContentPart(): String? {
        val cbtiEvaluationH5Data = CBTIEvaluationH5Data(scaleIds, chapterId)
        val cbtiEvaluationJson = JsonUtil.toJson(cbtiEvaluationH5Data)
        val uri = H5Uri.CBTI_OPEN_SCALES
        return uri.replace("{data}", cbtiEvaluationJson)
    }

    override fun registerHandler(sWebView: SWebView) {
        super.registerHandler(sWebView)
        sWebView.registerHandler("onCbtiChapterClick", object : SBridgeHandler() {
            override fun handler(data: String?) {
                super.handler(data)
                val type = object : TypeToken<H5BaseResponse<Map<String, Int>>>() {}
                val response = JsonUtil.fromJson<H5BaseResponse<Map<String, Int>>>(data, type.type)
                        ?: return
                val chapterId = response.result?.get("chapterId") ?: return
                CBTIWeekCoursePartActivity.show(this@CBTIEvaluationWebActivity, chapterId)
            }
        })
    }
}