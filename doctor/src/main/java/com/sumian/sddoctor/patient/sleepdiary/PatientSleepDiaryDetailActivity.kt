package com.sumian.sddoctor.patient.sleepdiary

import android.content.Intent
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.sddoctor.R
import com.sumian.sddoctor.base.SddBaseActivity
import com.sumian.sddoctor.constants.StatConstants

class PatientSleepDiaryDetailActivity : SddBaseActivity() {
    override fun showBackNav(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_patient_sleep_diary_detail
    }

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_TIME = "time"

        fun launch(userId: Int, time: Long) {
            val intent = Intent(ActivityUtils.getTopActivity(), PatientSleepDiaryDetailActivity::class.java)
            intent.putExtra(KEY_USER_ID, userId)
            intent.putExtra(KEY_TIME, time)
            ActivityUtils.startActivity(intent)
        }
    }

    override fun getPageName(): String {
        return StatConstants.page_patient_dairy_detail
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.sleep_diary)

        val userId = intent.getIntExtra(KEY_USER_ID, 0)
        val time = intent.getLongExtra(KEY_TIME, 0L)
        val sleepRecordFragment = SleepRecordFragment.newInstance(userId, time)
        supportFragmentManager.beginTransaction().add(R.id.vg_fragment_container, sleepRecordFragment).commit()
    }
}