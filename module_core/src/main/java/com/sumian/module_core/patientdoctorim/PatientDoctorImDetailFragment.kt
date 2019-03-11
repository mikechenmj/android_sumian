package com.sumian.module_core.patientdoctorim

import com.sumian.common.base.BaseFragment
import kotlinx.android.synthetic.main.core_im_fragment_patient_doctor_im_detail.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent


/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/8 10:32
 * desc   :
 * version: 1.0
 */
class PatientDoctorImDetailFragment : BaseFragment() {
    override fun getLayoutId(): Int {
        return com.sumian.module_core.R.layout.core_im_fragment_patient_doctor_im_detail
    }

    override fun initWidget() {
        super.initWidget()
        KeyboardVisibilityEvent.setEventListener(activity!!) { isOpen ->
            if (isOpen) {
                input_box_view.showRecordVoicePanel(false)
            }
        }

    }
}