package com.sumian.sleepdoctor.cbti.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.view.View
import com.google.gson.reflect.TypeToken
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.base.SdBasePresenter
import com.sumian.sleepdoctor.base.SdBaseWebViewActivity
import com.sumian.sleepdoctor.h5.H5Uri
import com.sumian.sleepdoctor.utils.JsonUtil
import com.sumian.sleepdoctor.widget.dialog.SumianAlertDialog
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
class CBTIExerciseWebActivity : SdBaseWebViewActivity<SdBasePresenter<*>>() {

    private var courseId: Int = 0

    private var mCode: Int = 0xff

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

    private var sumianAlertDialog: SumianAlertDialog? = null

    override fun onBack(v: View?) {
        when (mCode) {
            0 -> {

                val intent = Intent().apply {
                    action = "finished"
                }

                LocalBroadcastManager.getInstance(this@CBTIExerciseWebActivity).sendBroadcastSync(intent)
                finish()
            }
            0xff -> {
                sumianAlertDialog = SumianAlertDialog(this)
                        .whitenLeft()
                        .setTitle(R.string.are_you_exit_practice)
                        .setMessage("退出后将不会保存此次填写记录")
                        .setLeftBtn(R.string.cancel, null)
                        .setRightBtn(R.string.sure) { finish() }

                sumianAlertDialog?.show()
            }
            else -> {
                finish()
            }
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        onBack(null)
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
                        mCode = it.code
                        LocalBroadcastManager.getInstance(this@CBTIExerciseWebActivity).sendBroadcastSync(Intent().apply {
                            action = "finished"
                        })
                        finish()
                    }
                }

            }
        })
    }

}