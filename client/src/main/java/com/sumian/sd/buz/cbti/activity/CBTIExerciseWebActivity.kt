package com.sumian.sd.buz.cbti.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.gson.reflect.TypeToken
import com.sumian.common.h5.bean.SBridgeResult
import com.sumian.common.h5.handler.SBridgeHandler
import com.sumian.common.h5.widget.SWebView
import com.sumian.common.utils.JsonUtil
import com.sumian.sd.base.SdBaseWebViewActivity
import com.sumian.sd.common.h5.H5Uri
import java.util.*

/**
 * Created by dq
 *
 * on 2018/7/19
 *
 * desc: 练习题 item
 */
class CBTIExerciseWebActivity : SdBaseWebViewActivity() {

    private var courseId: Int = 0
    private var mQuitWithoutCheck = false

    companion object {

        private const val EXTRAS_COURSE_ID = "com.sumian.sleepdoctor.extras.course.id"

        fun show(context: Context, courseId: Int) {

            val extras = Bundle().apply {
                putInt(EXTRAS_COURSE_ID, courseId)
            }

            val intent = Intent(context, CBTIExerciseWebActivity::class.java)
            intent.putExtras(extras)
            context.startActivity(intent)
        }

    }

    override fun initBundle(bundle: Bundle) {
        courseId = bundle.getInt(EXTRAS_COURSE_ID, 0)
    }

    override fun getUrlContentPart(): String {
        return H5Uri.CBTI_EXERCISES.replace("{course-id}", String.format(Locale.getDefault(), "%d", courseId))
    }

    override fun registerHandler(sWebView: SWebView) {
        super.registerHandler(sWebView)
        sWebView.registerHandler("onQuestionFinished", object : SBridgeHandler() {
            override fun handler(data: String) {

                val sBridgeResult = JsonUtil.fromJson<SBridgeResult<Any>>(data, object : TypeToken<SBridgeResult<Any>>() {

                }.type)

                sBridgeResult?.let {
                    if (it.code == 0) {
                        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(this@CBTIExerciseWebActivity).sendBroadcastSync(Intent().apply {
                            action = "finished"
                        })
                        finish()
                    }
                }
            }
        })
        sWebView.registerHandler("quitWithoutCheck", object : SBridgeHandler() {
            override fun handler(data: String?) {
                mQuitWithoutCheck = true
            }
        })
    }

}