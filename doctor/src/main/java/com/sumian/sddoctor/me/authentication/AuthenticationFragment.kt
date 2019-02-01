package com.sumian.sddoctor.me.authentication

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.ViewModelProviders
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.base.BaseFragment
import com.sumian.sddoctor.constants.Configs
import com.sumian.sddoctor.login.register.AuthenticateViewModel
import com.sumian.sddoctor.login.register.bean.RegisterInfo
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
    //    private var mName: String? = null
//    private var mHospital: String? = null
    private var mDepartment: String? = null
    private var mJobTitle: String? = null
    private var mDoctorLicensePath: String? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_register_authentication
    }

    companion object {
        private const val REQUEST_CODE_GET_DEPARTMENT = 100
        private const val REQUEST_CODE_GET_JOB_TITLE = 101
    }

    override fun initWidget(root: View?) {
        super.initWidget(root)
        tv_submit_audit.setOnClickListener { onSubmitClick() }
        sdv_department.setOnClickListener { startActivityForString(REQUEST_CODE_GET_DEPARTMENT) }
        sdv_job_title.setOnClickListener { startActivityForString(REQUEST_CODE_GET_JOB_TITLE) }
        ll_doctor_license_container.setOnClickListener { addPhotoIfNeed() }
        iv_doctor_license_delete.setOnClickListener { updateDoctorLicensePath(null) }
    }

    private fun startActivityForString(requestCode: Int) {
        ChooseStringActivity.launchForResult(this,
                requestCode,
                getString(if (requestCode == REQUEST_CODE_GET_DEPARTMENT) R.string.choose_department else R.string.choose_job_title),
                getString(if (requestCode == REQUEST_CODE_GET_DEPARTMENT) R.string.add_department else R.string.add_job_title),
                activity!!.resources.getStringArray(if (requestCode == REQUEST_CODE_GET_DEPARTMENT) R.array.departments else R.array.job_titles),
                if (requestCode == REQUEST_CODE_GET_DEPARTMENT) true else true,
                getString(if (requestCode == REQUEST_CODE_GET_DEPARTMENT) R.string.add_department_hint else R.string.add_job_title_hint),
                getString(if (requestCode == REQUEST_CODE_GET_DEPARTMENT) R.string.save else R.string.save),
                getString(if (requestCode == REQUEST_CODE_GET_DEPARTMENT) R.string.department else R.string.job_title),
                getString(if (requestCode == REQUEST_CODE_GET_DEPARTMENT) R.string.department else R.string.job_title),
                getString(if (requestCode == REQUEST_CODE_GET_DEPARTMENT) R.string.input_department_hint else R.string.input_job_title_hint),
                if (requestCode == REQUEST_CODE_GET_DEPARTMENT) Configs.DEPARTMENT_NAME_MIN_LENGTH else Configs.JOB_TITLE_NAME_MIN_LENGTH,
                if (requestCode == REQUEST_CODE_GET_DEPARTMENT) Configs.DEPARTMENT_NAME_MAX_LENGTH else Configs.JOB_TITLE_NAME_MAX_LENGTH
        )
    }

    override fun initData() {
        super.initData()
        mViewModel = ViewModelProviders.of(activity!!).get(AuthenticateViewModel::class.java)
    }

    private fun onSubmitClick() {
        if (!InputCheckUtil.checkInput(et_name, getString(R.string.name), Configs.DOCTOR_NAME_MIN_LENGTH, Configs.DOCTOR_NAME_MAX_LENGTH)) {
            return
        }
        if (!InputCheckUtil.checkInput(et_hospital, getString(R.string.hospital_name), Configs.HOSPITAL_NAME_MIN_LENGTH, Configs.HOSPITAL_NAME_MAX_LENGTH)) {
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
        if (TextUtils.isEmpty(mDoctorLicensePath)) {
            ToastUtils.showShort(getString(R.string.please_upload_doctor_license))
            return
        }
        val registerInfo = RegisterInfo(
                et_name.text.toString(),
                et_hospital.text.toString(),
                mDepartment!!,
                mJobTitle!!)
        showLoading()
        val call = AppManager.getHttpService().register(registerInfo)
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
        val string = ChooseStringActivity.getString(data)
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
                else -> super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }
}