package com.sumian.sleepdoctor.improve.doctor.fragment

import android.util.Log
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.app.AppManager
import com.sumian.sleepdoctor.base.BaseFragment
import com.sumian.sleepdoctor.improve.doctor.activity.ScanDoctorQrCodeActivity
import com.sumian.sleepdoctor.improve.doctor.bean.Doctor
import com.sumian.sleepdoctor.improve.doctor.contract.DoctorContract
import com.sumian.sleepdoctor.improve.doctor.presenter.DoctorPresenter
import com.sumian.sleepdoctor.widget.RequestScanQrCodeView
import kotlinx.android.synthetic.main.fragment_tab_doctor.*

/**
 * Created by jzz
 * on 2018/5/2.
 * desc:
 */
class DoctorFragment : BaseFragment<DoctorPresenter>(), RequestScanQrCodeView.OnGrantedCallback, DoctorContract.View {

    val TAG: String = DoctorFragment::class.java.javaClass.simpleName

    override fun getLayoutId(): Int {
        return R.layout.fragment_tab_doctor
    }

    override fun initData() {
        super.initData()
        if (AppManager.getAccountViewModel()?.userProfile?.isBindDoctor!!) {
            request_scan_qr_code_view.hide()

            val doctor = AppManager.getAccountViewModel()?.userProfile?.doctor
            doctor_detail_layout.invalidDoctor(doctor)
            if (doctor?.services == null) {
                mPresenter.getBindDoctorInfo(doctor?.id!!)
            }
        } else {
            doctor_detail_layout.hide()
            request_scan_qr_code_view.setFragment(this).setOnGrantedCallback(this).show()
        }
        Log.e("DoctorFragment------->", AppManager.getAccountViewModel().userProfile.toString())
    }

    override fun initPresenter() {
        super.initPresenter()
        DoctorPresenter.init(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        request_scan_qr_code_view.onRequestPermissionsResultDelegate(requestCode, permissions, grantResults)
    }

    override fun onGrantedSuccess() {
        ScanDoctorQrCodeActivity.show(context, ScanDoctorQrCodeActivity::class.java)
    }

    override fun onGetDoctorInfoSuccess(doctor: Doctor?) {
        doctor_detail_layout.invalidDoctor(doctor)
    }

    override fun onGetDoctorInfoFailed(error: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setPresenter(presenter: DoctorContract.Presenter?) {
        this.mPresenter = presenter as DoctorPresenter?
        Log.e(TAG, "setPresenter-------->")
    }
}
