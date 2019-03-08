package com.sumian.module_core.patientdoctorim

import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.LogUtils
import com.sumian.common.base.BaseFragment
import com.sumian.module_core.R
import kotlinx.android.synthetic.main.core_im_fragment_patient_doctor_im_detail.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/8 10:32
 * desc   :
 * version: 1.0
 */
class PatientDoctorImDetailFragment : BaseFragment() {
    override fun getLayoutId(): Int {
        return R.layout.core_im_fragment_patient_doctor_im_detail
    }

    override fun initWidget() {
        super.initWidget()
        KeyboardUtils.registerSoftInputChangedListener(activity, object : KeyboardUtils.OnSoftInputChangedListener {
            override fun onSoftInputChanged(height: Int) {
                LogUtils.d(height)
                val keyboardShow = height > 100
                input_box_view.showRecordVoicePanel(!keyboardShow)
            }
        }
        )
    }
}