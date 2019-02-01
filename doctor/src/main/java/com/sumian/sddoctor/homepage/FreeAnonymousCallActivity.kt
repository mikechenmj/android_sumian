package com.sumian.sddoctor.homepage

import android.os.Bundle
import android.text.TextUtils
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.base.BaseActivity
import com.sumian.sddoctor.booking.bean.GetIsHangingResponse
import com.sumian.sddoctor.login.login.bean.DoctorInfo
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.patient.bean.Patient
import com.sumian.sddoctor.util.ImageLoader
import com.sumian.sddoctor.widget.SumianAlertDialog
import kotlinx.android.synthetic.main.activity_free_anonymous_call.*

class FreeAnonymousCallActivity : BaseActivity() {
    private var mUserId = 0

    override fun getContentId(): Int {
        return R.layout.activity_free_anonymous_call
    }

    companion object {
        const val KEY_ID = "id"

        fun launch(id: Int) {
            val bundle = Bundle()
            bundle.putInt("id", id)
            ActivityUtils.startActivity(bundle, FreeAnonymousCallActivity::class.java)
        }
    }

    override fun initBundle(bundle: Bundle?) {
        super.initBundle(bundle)
        if (bundle == null) {
            return
        }
        mUserId = bundle.getInt(KEY_ID)
    }

    override fun initWidget() {
        super.initWidget()
        title_bar.setOnBackClickListener { finish() }
        dv_doctor.setOnClickListener {
            SumianAlertDialog(this)
                    .setTitle(R.string.modify_cellphone)
                    .setMessage(R.string.modify_cellphone_hint)
                    .setLeftBtn(R.string.confirm, null)
                    .show()
        }
        tv_call.setOnClickListener {
            recallPatient()
        }
    }

    override fun initData() {
        super.initData()
        updateHangingState()
        updateDoctorInfo()
        updatePatientInfo()
    }

    private fun updateHangingState() {
        val call = AppManager.getHttpService().getIsHanging(mUserId)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<GetIsHangingResponse>() {
            override fun onSuccess(response: GetIsHangingResponse?) {
                enableTvCall(response?.hanging ?: false)
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                enableTvCall(false)
            }
        })
    }

    private fun enableTvCall(enable: Boolean) {
        tv_call.isEnabled = enable
        tv_call.text = getString(if (enable) R.string.start_call else R.string.no_booking_no_call)
    }

    private fun recallPatient() {
        AppManager.getHttpService().recallPatient(mUserId)
                .enqueue(object : BaseSdResponseCallback<FreeCallResponse>() {
                    override fun onSuccess(response: FreeCallResponse?) {
                        LogUtils.d(response)
                        ToastUtils.showShort(response?.message)
                    }

                    override fun onFailure(errorResponse: ErrorResponse) {
                        ToastUtils.showShort(errorResponse.message)
                    }

                    override fun onFinish() {

                    }
                })
    }

    private fun updatePatientInfo() {
        val call = AppManager.getHttpService().getPatientDetail(mUserId)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<Patient>() {
            override fun onSuccess(response: Patient?) {
                if (response == null) {
                    return
                }
                dv_patient.labelText = response.name
                dv_patient.setContentText(formatPhoneNumber(response.mobile))
                if (response.avatar == null || TextUtils.isEmpty(response.avatar)) {
                    ImageLoader.load(mActivity, R.mipmap.ic_info_avatar_patient, dv_patient.typeIv)
                    ImageLoader.load(mActivity, R.mipmap.ic_info_avatar_patient, iv_patient_avatar)
                } else {
                    ImageLoader.load(mActivity, response.avatar!!, dv_patient.typeIv)
                    ImageLoader.load(mActivity, response.avatar!!, iv_patient_avatar)
                }
            }

            override fun onFailure(errorResponse: ErrorResponse) {

            }
        })
    }

    private fun updateDoctorInfo() {
        AppManager.getAccountViewModel().getDoctorInfo()
                .observe(this, Observer<DoctorInfo> { t ->
                    if (t?.avatar == null || t.avatar.isEmpty()) {
                        ImageLoader.load(mActivity, R.mipmap.ic_info_avatar_patient, dv_doctor.typeIv)
                        ImageLoader.load(mActivity, R.mipmap.ic_info_avatar_patient, iv_doctor_avatar)
                    } else {
                        ImageLoader.load(mActivity, t.avatar, dv_doctor.typeIv)
                        ImageLoader.load(mActivity, t.avatar, iv_doctor_avatar)
                    }
                    dv_doctor.setContentText(formatPhoneNumber(t?.mobile))
                    dv_doctor.labelText = t?.name
                })
    }

    private fun formatPhoneNumber(string: String?): String {
        if (string == null) {
            return ""
        }
        return if (string.length < 7) {
            string
        } else {
            string.substring(0, 3) + "****" + string.substring(7, string.length)
        }
    }
}
