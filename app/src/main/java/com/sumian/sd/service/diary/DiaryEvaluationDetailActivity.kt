package com.sumian.sd.service.diary

import android.content.Context
import android.content.Intent
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.sumian.common.h5.handler.SBridgeHandler
import com.sumian.common.h5.widget.SWebView
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.JsonUtil
import com.sumian.sd.app.AppManager
import com.sumian.sd.h5.SimpleWebActivity
import com.sumian.sd.main.MainActivity
import com.sumian.sd.network.callback.BaseSdResponseCallback
import com.sumian.sd.service.diary.bean.DiaryEvaluationData
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * <pre>
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/6/4 10:25
 * desc   :
 * version: 1.0
</pre> *
 */
class DiaryEvaluationDetailActivity : SimpleWebActivity() {

    companion object {

        /**
         * launch latest diary evaluation
         *
         * @param context context
         */
        @JvmStatic
        fun launchLatestEvaluation(context: Context) {
            val call = AppManager.getSdHttpService().getLatestDiaryEvaluation(null)
            call.enqueue(object : BaseSdResponseCallback<DiaryEvaluationData>() {
                override fun onFailure(errorResponse: ErrorResponse) {
                    LogUtils.d(errorResponse.message)
                }

                override fun onSuccess(response: DiaryEvaluationData?) {
                    if (response == null) {
                        return
                    }
                    launch(context, response.id)
                }
            })
        }

        @JvmStatic
        fun launch(context: Context, id: Int) {
            val intent = getLaunchIntent(context, id)
            ActivityUtils.startActivity(intent)
        }

        @JvmStatic
        fun getLaunchIntent(context: Context, id: Int): Intent {
            val payload = HashMap<String, Any>(2)
            payload["id"] = id
            val page = HashMap<String, Any>(2)
            page["page"] = "weeklyAssess"
            page["payload"] = payload
            return SimpleWebActivity.getLaunchIntentWithRouteData(context, JsonUtil.toJson(page), DiaryEvaluationDetailActivity::class.java)
        }
    }

    override fun registerHandler(sWebView: SWebView) {
        super.registerHandler(sWebView)
        sWebView.registerHandler("weeklyAssessFilling", object : SBridgeHandler() {
            override fun handler(data: String) {
                EventBus.getDefault().postSticky(DiaryEvaluationFilledEvent())
                LogUtils.d(data)
            }
        })
        sWebView.registerHandler("toDoctorService", object : SBridgeHandler() {
            override fun handler(data: String) {
                LogUtils.d(data)
                MainActivity.launch(MainActivity.TAB_2, null)
                finish()
            }
        })
    }
}
