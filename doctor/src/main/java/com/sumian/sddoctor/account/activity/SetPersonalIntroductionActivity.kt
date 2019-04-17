package com.sumian.sddoctor.account.activity

import android.app.Activity
import android.content.Intent
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.sddoctor.R
import com.sumian.sddoctor.base.SddBaseActivity
import com.sumian.sddoctor.login.login.bean.DoctorInfo
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
        tv_save.setOnClickListener { saveIntro() }
    }

    private fun saveIntro() {
        val intro = et_intro.text.toString()
        // todo save intro
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