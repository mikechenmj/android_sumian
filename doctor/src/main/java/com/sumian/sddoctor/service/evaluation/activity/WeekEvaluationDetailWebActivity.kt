@file:Suppress("UNUSED_ANONYMOUS_PARAMETER")

package com.sumian.sddoctor.service.evaluation.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.h5.handler.SBridgeHandler
import com.sumian.common.h5.widget.SWebView
import com.sumian.common.utils.JsonUtil
import com.sumian.sddoctor.h5.SimpleWebActivity
import com.sumian.sddoctor.patient.fragment.PatientInfoWebFragment
import com.sumian.sddoctor.patient.sleepdiary.PatientSleepDiaryDetailActivity
import com.sumian.sddoctor.service.publish.activity.ReplyDocActivity
import com.sumian.sddoctor.service.publish.activity.ReplyVoiceActivity
import com.sumian.sddoctor.service.publish.bean.Publish
import java.util.*

@Suppress("DEPRECATION")
/**
 * Created by  sm
 *
 * on 2018/9/28
 *
 *desc:睡眠日记 周评估详情
 *
 */
class WeekEvaluationDetailWebActivity : SimpleWebActivity() {

    companion object {
        private const val EXTRAS_EVALUATION_ID = "com.sumian.sleepdoctor.extras.evaluation.id"

        fun launch(context: Context, id: Int) {
            val intent = getLaunchIntent(context, id)
            ActivityUtils.startActivity(intent)
        }

        fun getLaunchIntent(context: Context, id: Int): Intent {
            val payload = HashMap<String, Any>(2)
            payload["id"] = id
            val page = HashMap<String, Any>(2)
            page["page"] = "weeklyAssess"
            page["payload"] = payload
            val intent = SimpleWebActivity.getLaunchIntentWithRouteData(context, JsonUtil.toJson(page), WeekEvaluationDetailWebActivity::class.java)
            intent.putExtra(EXTRAS_EVALUATION_ID, id)
            return intent
        }
    }

    override fun onStart() {
        super.onStart()
        reload()
    }

    private var mEvaluationId: Int = 0

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        mEvaluationId = bundle.getInt(EXTRAS_EVALUATION_ID, 0)
    }

    override fun initTitle(): String? {
        return "评估详情"
    }

    override fun registerHandler(sWebView: SWebView) {
        super.registerHandler(sWebView)
        sWebView.registerHandler("voiceReply") { data, function -> ReplyVoiceActivity.show(mEvaluationId, Publish.PUBLISH_EVALUATION_TYPE) }
        sWebView.registerHandler("textReply") { data, function -> ReplyDocActivity.show(mEvaluationId, Publish.PUBLISH_EVALUATION_TYPE) }
        sWebView.registerHandler("toSleepDiariesDetail", object : SBridgeHandler() {
            override fun handler(data: String?) {
                data?.let {
                    val diaryDetailData = JsonUtil.fromJson(it, PatientInfoWebFragment.DiaryDetailData::class.java)!!
                    PatientSleepDiaryDetailActivity.launch(diaryDetailData.userId, diaryDetailData.timeUnix * 1000L)
                }
            }
        })
    }

}