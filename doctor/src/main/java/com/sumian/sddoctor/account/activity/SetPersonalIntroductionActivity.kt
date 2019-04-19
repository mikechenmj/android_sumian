package com.sumian.sddoctor.account.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.base.SddBaseActivity
import com.sumian.sddoctor.login.login.bean.DoctorInfo
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.widget.text.EmptyTextWatcher
import kotlinx.android.synthetic.main.activity_set_personal_introduction.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/4/15 17:23
 * desc   :
 * version: 1.0
 */
class SetPersonalIntroductionActivity : SddBaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_set_personal_introduction
    }

    override fun initWidget() {
        super.initWidget()
        et_intro.addTextChangedListener(object : EmptyTextWatcher() {
            @SuppressLint("SetTextI18n")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                tv_text_length.text = "${s!!.length}/ 500"
            }
        })
        et_intro.setText(intent.getStringExtra(KEY_INTRO))
        tv_save.setOnClickListener { saveIntro() }
    }

    private fun saveIntro() {
        showLoading()
        val intro = et_intro.text.toString()
        val call = AppManager.getHttpService().updateDoctorInfo(mapOf("introduction" to intro))
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<DoctorInfo>() {
            override fun onSuccess(response: DoctorInfo?) {
                val intent = Intent()
                intent.putExtra(KEY_RESULT, response)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }

            override fun onFinish() {
                super.onFinish()
                dismissLoading()
            }
        })
    }

    companion object {
        private const val KEY_INTRO = "KEY_INTRO"
        private const val KEY_RESULT = "KEY_RESULT"
        fun startForResult(activity: Activity, intro: String?, requestCode: Int) {
            val intent = Intent(ActivityUtils.getTopActivity(), SetPersonalIntroductionActivity::class.java)
            intent.putExtra(KEY_INTRO, intro)
            ActivityUtils.startActivityForResult(activity, intent, requestCode)
        }

        fun getResultFromIntent(intent: Intent?): DoctorInfo? {
            return intent?.getParcelableExtra(KEY_RESULT)
        }
    }
}