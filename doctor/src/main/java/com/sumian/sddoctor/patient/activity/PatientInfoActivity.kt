package com.sumian.sddoctor.patient.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BaseActivity
import com.sumian.sddoctor.R
import com.sumian.sddoctor.constants.StatConstants
import com.sumian.sddoctor.patient.bean.Patient
import com.sumian.sddoctor.patient.fragment.PatientInfoWebFragment

/**
 * <pre>
 *     @author : sm

 *     e-mail : yaoqi.y@sumian.com
 *     time: 2018/6/29 17:37
 *
 *     version: 1.0
 *
 *     desc:患者资料
 *
 * </pre>
 */
class PatientInfoActivity : BaseActivity() {

    companion object {

        private const val EXTRAS_ID = "com.sumian.sddoctor.extras.id"
        private const val EXTRAS_FACED = "com.sumian.sddoctor.extras.faced"

        fun show(context: Context, patientId: Int, faced: Int) {
            val extras = Bundle().apply {
                putInt(EXTRAS_ID, patientId)
                putInt(EXTRAS_FACED, faced)
            }
            val intent = Intent(context, PatientInfoActivity::class.java)
            intent.putExtras(extras)
            ActivityUtils.startActivity(intent)
        }
    }

    private var mPatientId: Int = 0

    private var mFaced = Patient.UN_FACED_TYPE

    override fun getLayoutId(): Int {
        return R.layout.activity_main_patient_info
    }

    override fun getPageName(): String {
        return StatConstants.page_patient_detail
    }

    override fun initBundle(bundle: Bundle) {
            mPatientId = bundle.getInt(EXTRAS_ID, 0)
            mFaced = bundle.getInt(EXTRAS_FACED, Patient.UN_FACED_TYPE)
    }

    override fun initData() {
        super.initData()
        supportFragmentManager?.beginTransaction()
                ?.add(R.id.fragment_container, PatientInfoWebFragment.newInstance(mPatientId), PatientInfoWebFragment::class.java.simpleName)
                ?.commitAllowingStateLoss()
    }

    override fun onBackPressed() {
        val fragmentByTag = supportFragmentManager.findFragmentByTag(PatientInfoWebFragment::class.java.simpleName)

        if (fragmentByTag != null && fragmentByTag is PatientInfoWebFragment && !fragmentByTag.onBack()) {
            super.onBackPressed()
        }
    }

}