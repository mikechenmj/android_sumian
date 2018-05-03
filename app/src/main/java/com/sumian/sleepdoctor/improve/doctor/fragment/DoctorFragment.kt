package com.sumian.sleepdoctor.improve.doctor.fragment

import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.app.AppManager
import com.sumian.sleepdoctor.base.BaseFragment
import com.sumian.sleepdoctor.widget.RequestScanQrCodeView
import kotlinx.android.synthetic.main.fragment_tab_doctor.*

/**
 * Created by jzz
 * on 2018/5/2.
 * desc:
 */
class DoctorFragment : BaseFragment<Any>(), RequestScanQrCodeView.OnGrantedCallback {


    override fun getLayoutId(): Int {
        return R.layout.fragment_tab_doctor
    }

    override fun initData() {
        super.initData()

        if (AppManager.getAccountViewModel()?.userProfile?.isBindDoctor!!) {
            request_scan_qr_code_view.hide()
        } else {
            request_scan_qr_code_view.setFragment(this).setOnGrantedCallback(this).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        request_scan_qr_code_view.onRequestPermissionsResultDelegate(requestCode, permissions, grantResults)
    }

    override fun onGrantedSuccess() {
        //TODO

    }
}
