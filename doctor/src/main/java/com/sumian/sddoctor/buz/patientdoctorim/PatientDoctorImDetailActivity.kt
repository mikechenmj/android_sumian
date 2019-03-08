package com.sumian.sddoctor.buz.patientdoctorim

import com.sumian.module_core.patientdoctorim.PatientDoctorImDetailFragment
import com.sumian.sddoctor.R
import com.sumian.sddoctor.base.SddBaseActivity

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/8 10:58
 * desc   :
 * version: 1.0
 */
class PatientDoctorImDetailActivity : SddBaseActivity() {
    private lateinit var mPatientDoctorImDetailFragment : PatientDoctorImDetailFragment
    override fun getLayoutId(): Int {
        return R.layout.im_activity_patient_doctor_im_detail
    }

    override fun initWidget() {
        super.initWidget()
        initFragment()
    }

    private fun initFragment() {
        mPatientDoctorImDetailFragment = PatientDoctorImDetailFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, mPatientDoctorImDetailFragment).commit()
    }
}