package com.sumian.sddoctor.patient.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.widget.TitleBar
import com.sumian.sddoctor.R
import com.sumian.sddoctor.base.SddBaseViewModelActivity
import com.sumian.sddoctor.constants.StatConstants
import com.sumian.sddoctor.patient.bean.Patient
import com.sumian.sddoctor.patient.contract.ModifyPatientTagContract
import com.sumian.sddoctor.patient.presenter.ModifyPatientTagPresenter
import com.sumian.sddoctor.util.ResUtils
import kotlinx.android.synthetic.main.activity_main_modify_patient_tag.*

/**
 * Created by sm
 *
 * on 2018/8/27
 *
 * desc:  修改患者 已面诊/未面诊状态标签
 *
 * */
class ModifyPatientTagActivity : SddBaseViewModelActivity<ModifyPatientTagPresenter>(), View.OnClickListener, ModifyPatientTagContract.View, TitleBar.OnMenuClickListener {

    companion object {

        private const val EXTRAS_PATIENT_ID = "com.sumian.sddoctor.extras.patient.id"

        @JvmStatic
        fun show(patientId: Int) {
            val topActivity = ActivityUtils.getTopActivity()
            topActivity?.let {
                val intent = Intent(it, ModifyPatientTagActivity::class.java).apply {
                    putExtra(EXTRAS_PATIENT_ID, patientId)
                }
                it.startActivity(intent)
            }
        }
    }

    private var mPatientId: Int = 0

    private var mFaced: Int = Patient.UN_FACED_TYPE

    override fun showBackNav(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main_modify_patient_tag
    }

    override fun getPageName(): String {
        return StatConstants.page_patient_tags
    }
    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        this.mPatientId = bundle.getInt(EXTRAS_PATIENT_ID, 0)
    }

    override fun initWidgetBefore() {
        super.initWidgetBefore()
        this.mViewModel = ModifyPatientTagPresenter.init(this)
    }

    override fun initWidget() {
        super.initWidget()
        mTitleBar.setTitle(R.string.modify_patient_tags)
        mTitleBar.setMenuText(getString(R.string.title_finish))
        mTitleBar.setMenuVisibility(View.VISIBLE)
        mTitleBar.setOnMenuClickListener(this)
        invalidFaced()
        tv_faced.setOnClickListener(this)
        tv_none_faced.setOnClickListener(this)
    }

    override fun initData() {
        super.initData()
        mViewModel?.getPatient(mPatientId)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_faced -> {
                mFaced = Patient.FACED_TYPE

                tv_faced.isActivated = true
                tv_faced.setTextColor(ResUtils.getColor(R.color.b2_color))

                tv_none_faced.isActivated = false
                tv_none_faced.setTextColor(ResUtils.getColor(R.color.t2_color))
            }
            R.id.tv_none_faced -> {
                mFaced = Patient.UN_FACED_TYPE

                tv_none_faced.isActivated = true
                tv_none_faced.setTextColor(ResUtils.getColor(R.color.b2_color))

                tv_faced.isActivated = false
                tv_faced.setTextColor(ResUtils.getColor(R.color.t2_color))
            }
        }
    }

    override fun onMenuClick(v: View) {
        mViewModel?.consultedPatient(mPatientId, mFaced)
    }

    override fun onGetPatientSuccess(patient: Patient) {
        this.mFaced = patient.consulted
        invalidFaced()
    }

    override fun onGetPatientFailed(error: String) {
        ToastUtils.showShort(error)
    }

    override fun onConsultedSuccess(patient: Patient) {
        onGetPatientSuccess(patient)
        finish()
    }

    override fun onConsultedFailed(error: String) {
        ToastUtils.showShort(error)
    }

    private fun invalidFaced() {
        if (mFaced == Patient.UN_FACED_TYPE) {
            tv_none_faced.isActivated = true
            tv_none_faced.setTextColor(ResUtils.getColor(R.color.b2_color))

            tv_faced.isActivated = false
            tv_faced.setTextColor(ResUtils.getColor(R.color.t2_color))
        } else {
            tv_faced.isActivated = true
            tv_faced.setTextColor(ResUtils.getColor(R.color.b2_color))

            tv_none_faced.setTextColor(ResUtils.getColor(R.color.t2_color))
            tv_none_faced.isActivated = false
        }
    }

}