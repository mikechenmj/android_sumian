package com.sumian.sddoctor.login.login

import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.base.SddBaseActivity
import com.sumian.sddoctor.login.login.bean.DoctorInfo
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import kotlinx.android.synthetic.main.activity_choose_identity.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/4/15 15:55
 * desc   :
 * version: 1.0
 */
class ChooseIdentityActivity : SddBaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_choose_identity
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.choose_identity)
        tv_doctor.setOnClickListener { changeIdentity(DoctorInfo.TYPE_DOCTOR) }
        tv_counselor.setOnClickListener { changeIdentity(DoctorInfo.TYPE_COUNSELOR) }
    }

    override fun showBackNav(): Boolean {
        return true
    }

    /**
     * type: 1 doctor, 2 counselor
     */
    private fun changeIdentity(type: Int) {
        val isDoctor = type == DoctorInfo.TYPE_DOCTOR
        tv_doctor.isSelected = isDoctor
        tv_counselor.isSelected = !isDoctor
        tv_doctor.setTextColor(resources.getColor(if (isDoctor) R.color.b3_color else R.color.t2_color))
        tv_counselor.setTextColor(resources.getColor(if (!isDoctor) R.color.t4_color else R.color.t2_color))
        tv_doctor.setCompoundDrawablesWithIntrinsicBounds(0, if (isDoctor) R.drawable.login_icon_doctor_selected else R.drawable.login_icon_doctor_default, 0, 0)
        tv_counselor.setCompoundDrawablesWithIntrinsicBounds(0, if (!isDoctor) R.drawable.login_icon_counselor_selected else R.drawable.login_icon_counselor_default, 0, 0)

        val call = AppManager.getHttpService().updateDoctorInfo(mapOf("type" to type))
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<DoctorInfo>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }

            override fun onSuccess(response: DoctorInfo?) {
//                AppManager.getAccountViewModel().updateDoctorInfo(response)
                AppManager.getAccountViewModel().updateDoctorInfo(response, true)
                ActivityUtils.startActivity(SetInviteCodeActivity::class.java)
            }
        })
    }
}