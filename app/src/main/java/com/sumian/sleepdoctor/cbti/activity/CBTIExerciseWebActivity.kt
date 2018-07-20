package com.sumian.sleepdoctor.cbti.activity

import android.content.Context
import android.os.Bundle
import com.google.gson.reflect.TypeToken
import com.sumian.sleepdoctor.base.BasePresenter
import com.sumian.sleepdoctor.base.BaseWebViewActivity
import com.sumian.sleepdoctor.h5.H5Uri
import com.sumian.sleepdoctor.utils.JsonUtil
import com.sumian.sleepdoctor.widget.webview.SBridgeHandler
import com.sumian.sleepdoctor.widget.webview.SBridgeResult
import com.sumian.sleepdoctor.widget.webview.SWebView
import java.util.*

/**
 * Created by dq
 *
 * on 2018/7/19
 *
 * desc: 练习题 item
 */
class CBTIExerciseWebActivity : BaseWebViewActivity<BasePresenter<*>>() {

    private var courseId: Int = 0

    private var isFinished: Boolean = false

    companion object {

        private const val EXTRAS_COURSE_ID = "com.sumian.sleepdoctor.extras.course.id"

        fun show(context: Context, courseId: Int) {

            val extras = Bundle().apply {
                putInt(EXTRAS_COURSE_ID, courseId)
            }
            show(context, CBTIExerciseWebActivity::class.java, extras)
        }

    }

    override fun initBundle(bundle: Bundle?): Boolean {
        bundle?.let {
            courseId = it.getInt(EXTRAS_COURSE_ID, 0)
        }

        return super.initBundle(bundle)
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
                        finish()
                    }
                }

            }
        })
    }

}