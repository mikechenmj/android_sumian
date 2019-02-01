@file:Suppress("DEPRECATION")

package com.sumian.sddoctor.service.advisory.activity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.mvp.IPresenter
import com.sumian.sddoctor.R
import com.sumian.sddoctor.base.SddBaseActivity
import com.sumian.sddoctor.patient.fragment.PatientInfoWebFragment
import com.sumian.sddoctor.service.advisory.fragment.AdvisoryDetailFragment
import kotlinx.android.synthetic.main.activity_main_advisory_detail.*

/**
 * Created by sm
 *
 * on 2018/8/28
 *
 * desc:医生图文咨询详情
 *
 */
class AdvisoryDetailActivity : SddBaseActivity<IPresenter>() {

    companion object {

        private const val EXTRAS_ADVISORY_ID = "com.sumian.sddoctor.extras.advisory.id"
        private const val EXTRAS_PATIENT_ID = "com.sumian.sddoctor.extras.patient.id"

        @JvmStatic
        fun show(advisoryId: Int, patientId: Int) {
            val topActivity = ActivityUtils.getTopActivity()
            topActivity?.let {
                it.startActivity(Intent(it, AdvisoryDetailActivity::class.java).apply {
                    putExtra(EXTRAS_ADVISORY_ID, advisoryId)
                    putExtra(EXTRAS_PATIENT_ID, patientId)
                })
            }
        }

        @JvmStatic
        fun getLaunchIntent(advisoryId: Int, patientId: Int): Intent {
            return Intent(ActivityUtils.getTopActivity(), AdvisoryDetailActivity::class.java).apply {
                putExtra(EXTRAS_ADVISORY_ID, advisoryId)
                putExtra(EXTRAS_PATIENT_ID, patientId)
            }
        }
    }

    private var mAdvisoryId: Int = 0
    private var mPatientId: Int = 0

    override fun showBackNav(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main_advisory_detail
    }

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        this.mAdvisoryId = bundle.getInt(EXTRAS_ADVISORY_ID, 0)
        this.mPatientId = bundle.getInt(EXTRAS_PATIENT_ID, 0)
    }

//    override fun getTitleBarTitle(): String? {
//        return getString(R.string.doc_advisory)
//    }

    override fun initWidget() {
        super.initWidget()
        mTitleBar.setTitle(R.string.doc_advisory)
        view_pager?.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> AdvisoryDetailFragment.newInstance(mAdvisoryId)
                    1 -> PatientInfoWebFragment.newInstance(mPatientId, false)
                    else -> throw IllegalArgumentException("invalid position")
                }
            }

            override fun getCount(): Int {
                return 2
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return when (position) {
                    0 -> "问题详情"
                    1 -> "患者档案"
                    else -> throw IllegalArgumentException("invalid position")
                }
            }
        }

        tab_layout?.setupWithViewPager(view_pager, true)
    }

}