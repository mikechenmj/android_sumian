package com.sumian.sddoctor.account.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseViewModelActivity
import com.sumian.sddoctor.R
import com.sumian.sddoctor.account.contract.AccountContract
import com.sumian.sddoctor.account.presenter.AccountPresenter
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.constants.Configs
import com.sumian.sddoctor.constants.StatConstants
import com.sumian.sddoctor.login.login.bean.DoctorInfo
import com.sumian.sddoctor.util.InputCheckUtil
import kotlinx.android.synthetic.main.activity_modify_name.*

@Suppress("DEPRECATION")
class ModifyDoctorInfoActivity : BaseViewModelActivity<AccountPresenter>(), View.OnClickListener, AccountContract.View {

    override fun onModifySuccess(doctorInfo: DoctorInfo) {
        finish()
    }

    override fun onModifyFailed(error: String) {
        ToastUtils.showShort(error)
    }

    private var modifyType: Int = MODIFY_TYPE_NAME

    companion object {
        const val EXTRAS_MODIFY_TYPE = "com.sumian.sddoctor.extras.modify.type"
        const val MODIFY_TYPE_NAME = 0x01
        const val MODIFY_TYPE_HOSPITAL = 0x02
        const val MODIFY_TYPE_DEPARTMENT = 0x03
        const val MODIFY_TYPE_JOB_TITLE = 0x04

        fun show(context: Context, modifyType: Int = MODIFY_TYPE_NAME) {
            val intent = Intent(context, ModifyDoctorInfoActivity::class.java)
            intent.putExtra(EXTRAS_MODIFY_TYPE, modifyType)
            ActivityUtils.startActivity(intent)
        }
    }


    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        modifyType = bundle.getInt(EXTRAS_MODIFY_TYPE, MODIFY_TYPE_NAME)

    }

    override fun onClick(v: View?) {
        val input = et_name.text.toString().trim()
        if (!InputCheckUtil.checkInput(input, getFieldName(), getFieldMinLength(), getFieldMaxLength())) {
            return
        }
        mViewModel?.modifyDoctorInfo(modifyType, input)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_modify_name
    }

    override fun initWidgetBefore() {
        mViewModel = AccountPresenter.init(this)
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.modify_name)
        bt_finish.setOnClickListener(this)
    }

    override fun showBackNav(): Boolean {
        return true
    }

    override fun initData() {
        super.initData()
        val doctorInfo = AppManager.getAccountViewModel().getDoctorInfo().value ?: return
        when (modifyType) {
            MODIFY_TYPE_NAME -> initUI(R.string.title_modify_name, R.string.name, doctorInfo.name, R.string.please_input_your_name)
            MODIFY_TYPE_HOSPITAL -> initUI(R.string.title_modify_hospital, R.string.hospital, doctorInfo.hospital, R.string.please_input_your_hospital)
            MODIFY_TYPE_DEPARTMENT -> initUI(R.string.title_modify_department, R.string.department, doctorInfo.department, R.string.please_input_your_department)
            else -> Unit
        }
    }

    private fun getFieldName(): String {
        val strRes: Int = when (modifyType) {
            MODIFY_TYPE_NAME -> R.string.name
            MODIFY_TYPE_HOSPITAL -> R.string.hospital
            MODIFY_TYPE_DEPARTMENT -> R.string.department
            else -> 0
        }
        return if (strRes == 0) {
            ""
        } else {
            getString(strRes)
        }
    }

    private fun getFieldMinLength(): Int {
        return when (modifyType) {
            MODIFY_TYPE_NAME -> Configs.DOCTOR_NAME_MIN_LENGTH
            MODIFY_TYPE_HOSPITAL -> Configs.HOSPITAL_NAME_MIN_LENGTH
            MODIFY_TYPE_DEPARTMENT -> Configs.DEPARTMENT_NAME_MIN_LENGTH
            else -> 1
        }
    }

    private fun getFieldMaxLength(): Int {
        return when (modifyType) {
            MODIFY_TYPE_NAME -> Configs.DOCTOR_NAME_MAX_LENGTH
            MODIFY_TYPE_HOSPITAL -> Configs.HOSPITAL_NAME_MAX_LENGTH
            MODIFY_TYPE_DEPARTMENT -> Configs.DEPARTMENT_NAME_MAX_LENGTH
            else -> 100
        }
    }

    private fun initUI(titleRes: Int, etLabelRes: Int, etText: String, etHint: Int) {
        setTitle(titleRes)
        tv_label.text = getString(etLabelRes)
        et_name.setText(etText)
        et_name.hint = getString(etHint)
    }


    override fun showLoading() {
        super<BaseViewModelActivity>.showLoading()
    }

    override fun dismissLoading() {
        super<BaseViewModelActivity>.dismissLoading()
    }
}
