package com.sumian.sddoctor.service.report.activity

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ReportFragment
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.sddoctor.R
import com.sumian.sddoctor.base.SddBaseActivity
import com.sumian.sddoctor.service.report.fragment.DailyReportFragment

/**
 * Created by dq
 *
 * on 2018/9/14
 *
 * desc:睡眠数据监测报告
 */
class ReportActivity : SddBaseActivity() {

    companion object {
        @JvmStatic
        fun show(patientId: Int, sleepDateTime: Long) {
            ActivityUtils.getTopActivity()?.let {
                it.startActivity(Intent(it, ReportActivity::class.java).apply {
                    putExtra(DailyReportFragment.ARGS_PATIENT_ID, patientId)
                    putExtra(DailyReportFragment.ARGS_PATIENT_SLEEP_DATA_TIME, sleepDateTime)
                })
            }
        }
    }

    private var mSelectedTime = System.currentTimeMillis()
    private var mPatientId = 0

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        this.mPatientId = bundle.getInt(DailyReportFragment.ARGS_PATIENT_ID, 0)
        this.mSelectedTime = bundle.getLong(DailyReportFragment.ARGS_PATIENT_SLEEP_DATA_TIME, 0)
    }

    override fun showBackNav(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main_report
    }

    override fun initWidget() {
        super.initWidget()
        mTitleBar.setTitle(R.string.monitor_sleep_data)
        val reportFragment = DailyReportFragment.newInstance(mPatientId, mSelectedTime)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, reportFragment, ReportFragment::class.java.simpleName)
                .commitNowAllowingStateLoss()
    }

    private fun invalidTitleBar() {
        mTitleBar.setTitle(R.string.monitor_sleep_data)
    }
}