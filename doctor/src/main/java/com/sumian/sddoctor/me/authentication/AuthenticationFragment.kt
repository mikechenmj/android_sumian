package com.sumian.sddoctor.me.authentication

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.statistic.StatUtil
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.base.BaseFragment
import com.sumian.sddoctor.constants.Configs
import com.sumian.sddoctor.constants.StatConstants
import com.sumian.sddoctor.login.login.bean.DoctorInfo
import com.sumian.sddoctor.login.register.AuthenticateViewModel
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.oss.OssEngine
import com.sumian.sddoctor.oss.OssResponse
import com.sumian.sddoctor.util.ImageLoader
import com.sumian.sddoctor.util.InputCheckUtil
import com.sumian.sddoctor.widget.sheet.SelectPictureBottomSheet
import kotlinx.android.synthetic.main.fragment_register_authentication.*

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/28 17:37
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class AuthenticationFragment : BaseFragment() {
    private lateinit var mViewModel: AuthenticateViewModel
    private var mDepartment: String? = null
    private var mJobTitle: String? = null
    private var mCounselorQuality: String? = null
    private var mCounselorExperience: String? = null
    private var mCounselorExperienceIndex = 0
    private var mDoctorLicensePath: String? = null
    private val mDoctorInfo: DoctorInfo by lazy { AppManager.getAccountViewModel().getDoctorInfo().value!! }
    private var mType = mDoctorInfo.type

    override fun getLayoutId(): Int {
        return R.layout.fragment_register_authentication
    }

    companion object {
        private const val REQUEST_CODE_GET_DEPARTMENT = 100
        private const val REQUEST_CODE_GET_JOB_TITLE = 101
        private const val REQUEST_CODE_GET_COUNSELOR_QUALITY = 102
        private const val REQUEST_CODE_GET_COUNSELOR_EXPERIENCE = 103
    }

    override fun initWidget(root: View?) {
        super.initWidget(root)
        tv_submit_audit.setOnClickListener {
            StatUtil.event(StatConstants.click_doctor_verify_page_submit_btn)
            onSubmitClick()
        }
        sdv_department.setOnClickListener { startActivityForString(REQUEST_CODE_GET_DEPARTMENT) }
        sdv_job_title.setOnClickListener { startActivityForString(REQUEST_CODE_GET_JOB_TITLE) }
        sdv_counselor_experience.setOnClickListener { startActivityForString(REQUEST_CODE_GET_COUNSELOR_EXPERIENCE) }
        sdv_counselor_qualification.setOnClickListener { startActivityForString(REQUEST_CODE_GET_COUNSELOR_QUALITY) }
        ll_doctor_license_container.setOnClickListener { addPhotoIfNeed() }
        iv_doctor_license_delete.setOnClickListener { updateDoctorLicensePath(null) }
        switchUIByType()
        val isDoctor = mType == DoctorInfo.TYPE_DOCTOR
        rb_doctor.isChecked = isDoctor
        rb_counselor.isChecked = !isDoctor
        vg_type.setOnCheckedChangeListener { _, checkedId ->
            run {
                mType = if (checkedId == R.id.rb_doctor) DoctorInfo.TYPE_DOCTOR else DoctorInfo.TYPE_COUNSELOR
                switchUIByType()
            }
        }
    }

    /**
     * 根据用户类型改变UI
     */
    private fun switchUIByType() {
        val isDoctor = isDoctor()
        vg_doctor.isVisible = isDoctor
        vg_counselor.isVisible = !isDoctor
        tv_upload_material_label.text = getString(if (isDoctor) R.string.please_upload_your_doctor_license else R.string.please_upload_your_counselor_license)
        tv_upload_material_sub_label.text = getString(if (isDoctor) R.string.please_upload_your_doctor_license else R.string.please_upload_your_counselor_license)
        tv_upload_material_desc.text = getString(if (isDoctor) R.string.please_upload_your_doctor_license_desc else R.string.please_upload_your_counselor_license_desc)
    }

    private fun startActivityForString(requestCode: Int) {
        val params = when (requestCode) {
            REQUEST_CODE_GET_DEPARTMENT -> ChooseStringActivity.createParams(
                    context = activity!!,
                    title = R.string.choose_department,
                    stringArr = R.array.departments,
                    canAddMore = true,
                    addMoreHint = R.string.add_department_hint,
                    inputStringPageTitle = R.string.add_department,
                    inputStringPageMenu = R.string.save,
                    inputStringPageLabel = R.string.department,
                    inputStringPageFieldName = R.string.department,
                    inputStringPageInputHint = R.string.input_department_hint,
                    inputStringPageInputMinLength = Configs.DEPARTMENT_NAME_MIN_LENGTH,
                    inputStringPageInputMaxLength = Configs.DEPARTMENT_NAME_MAX_LENGTH
            )
            REQUEST_CODE_GET_JOB_TITLE -> ChooseStringActivity.createParams(
                    context = activity!!,
                    title = R.string.choose_job_title,
                    stringArr = R.array.job_titles,
                    canAddMore = true,
                    addMoreHint = R.string.add_job_title_hint,
                    inputStringPageTitle = R.string.add_job_title,
                    inputStringPageMenu = R.string.save,
                    inputStringPageLabel = R.string.job_title,
                    inputStringPageFieldName = R.string.job_title,
                    inputStringPageInputHint = R.string.input_job_title_hint,
                    inputStringPageInputMinLength = Configs.JOB_TITLE_NAME_MIN_LENGTH,
                    inputStringPageInputMaxLength = Configs.JOB_TITLE_NAME_MAX_LENGTH
            )
            REQUEST_CODE_GET_COUNSELOR_QUALITY -> ChooseStringActivity.createParams(
                    context = activity!!,
                    title = R.string.counselor_qualification_list_page_title,
                    stringArr = R.array.counselor_qualification_list,
                    canAddMore = true,
                    addMoreHint = R.string.counselor_qualification_add_hint,
                    inputStringPageTitle = R.string.counselor_qualification_input_page_title,
                    inputStringPageMenu = R.string.save,
                    inputStringPageLabel = R.string.counselor_qualification,
                    inputStringPageFieldName = R.string.counselor_qualification,
                    inputStringPageInputHint = R.string.counselor_qualification_input_hint,
                    inputStringPageInputMinLength = Configs.COUNSELOR_QUALIFICATION_MIN_LENGTH,
                    inputStringPageInputMaxLength = Configs.COUNSELOR_QUALIFICATION_MAX_LENGTH
            )
            else -> ChooseStringActivity.createParams(
                    context = activity!!,
                    title = R.string.counselor_experience_years_list_page_title,
                    stringArr = R.array.counselor_experience_years,
                    canAddMore = false,
                    addMoreHint = 0,
                    inputStringPageTitle = 0,
                    inputStringPageMenu = 0,
                    inputStringPageLabel = 0,
                    inputStringPageFieldName = 0,
                    inputStringPageInputHint = 0,
                    inputStringPageInputMinLength = 0,
                    inputStringPageInputMaxLength = 0
            )
        }
        ChooseStringActivity.launchForResult(this, requestCode, params)
    }

    override fun initData() {
        super.initData()
        mViewModel = ViewModelProviders.of(activity!!).get(AuthenticateViewModel::class.java)
    }

    private fun isDoctor(): Boolean {
        return mType == DoctorInfo.TYPE_DOCTOR
    }

    private fun onSubmitClick() {
        if (!InputCheckUtil.checkInput(et_name, getString(R.string.name), Configs.DOCTOR_NAME_MIN_LENGTH, Configs.DOCTOR_NAME_MAX_LENGTH)) {
            return
        }

        val map: MutableMap<String, Any> = mutableMapOf("name" to et_name.text.toString())
        if (isDoctor()) {
            val hospital = et_hospital.text.toString()
            if (!InputCheckUtil.checkInput(hospital, getString(R.string.hospital_name), Configs.HOSPITAL_NAME_MIN_LENGTH, Configs.HOSPITAL_NAME_MAX_LENGTH)) {
                return
            }
            if (TextUtils.isEmpty(mDepartment)) {
                ToastUtils.showShort(getString(R.string.please_choose_department))
                return
            }
            if (TextUtils.isEmpty(mJobTitle)) {
                ToastUtils.showShort(getString(R.string.please_choose_job_title))
                return
            }
            map["hospital"] = hospital
            map["department"] = mDepartment!!
            map["title"] = mJobTitle!!
        } else {
            if (TextUtils.isEmpty(mCounselorQuality)) {
                ToastUtils.showShort(getString(R.string.please_input_counselor_qualification))
                return
            }
            if (TextUtils.isEmpty(mCounselorExperience)) {
                ToastUtils.showShort(getString(R.string.please_input_counselor_experience))
                return
            }
            val caseHours = et_work_experience_hours.text.toString()
            if (TextUtils.isEmpty(caseHours)) {
                ToastUtils.showShort(getString(R.string.please_input_counselor_case_hours))
                return
            }
            val caseHoursInt = caseHours.toInt()
            if (caseHoursInt > 300_000) {
                ToastUtils.showShort(getString(R.string.counselor_case_hours_too_long))
                return
            }
            map["qualification"] = mCounselorQuality!!
            map["experience"] = mCounselorExperienceIndex
            map["cases_time"] = caseHoursInt
        }
        if (TextUtils.isEmpty(mDoctorLicensePath)) {
            ToastUtils.showShort(getString(R.string.please_upload_identity_material))
            return
        }
        showLoading()
        val call = AppManager.getHttpService().register(map)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<OssResponse>() {
            override fun onSuccess(response: OssResponse?) {
                OssEngine.uploadFile(response!!, mDoctorLicensePath!!, object : OssEngine.UploadCallback {
                    override fun onSuccess(response: String?) {
                        LogUtils.d(response)
                        dismissLoading()
                        mViewModel.goNextStep()
                    }

                    override fun onFailure(errorCode: String?, message: String?) {
                        LogUtils.d(message)
                        ToastUtils.showShort(message)
                        dismissLoading()
                    }
                })
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                LogUtils.d(errorResponse)
                ToastUtils.showShort(errorResponse.message)
                dismissLoading()
            }
        })
    }

    private fun addPhotoIfNeed() {
        if (!TextUtils.isEmpty(mDoctorLicensePath)) {
            return
        }
        SelectPictureBottomSheet.show(fragmentManager) { filePath ->
            updateDoctorLicensePath(filePath)
        }
    }

    private fun updateDoctorLicensePath(path: String?) {
        mDoctorLicensePath = path
        iv_doctor_license_delete.visibility = if (path == null) GONE else VISIBLE
        tv_doctor_license_upload_hint.visibility = if (path == null) VISIBLE else GONE
        if (path == null) {
            ImageLoader.load(activity!!, R.drawable.ic_login_btn_uploadphoto, iv_doctor_license)
        } else {
            ImageLoader.load(activity!!, path, iv_doctor_license)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val index = ChooseStringActivity.getResultIndex(data)
        val string = ChooseStringActivity.getResultString(data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_GET_DEPARTMENT -> {
                    sdv_department.setContentText(string)
                    mDepartment = string
                }
                REQUEST_CODE_GET_JOB_TITLE -> {
                    sdv_job_title.setContentText(string)
                    mJobTitle = string
                }
                REQUEST_CODE_GET_COUNSELOR_QUALITY -> {
                    sdv_counselor_qualification.setContentText(string)
                    mCounselorQuality = string
                }
                REQUEST_CODE_GET_COUNSELOR_EXPERIENCE -> {
                    sdv_counselor_experience.setContentText(string)
                    mCounselorExperience = string
                    mCounselorExperienceIndex = index
                }
                else -> super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }
}