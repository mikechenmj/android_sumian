package com.sumian.sleepdoctor.improve.doctor.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentActivity
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.app.AppManager
import com.sumian.sleepdoctor.improve.doctor.activity.ScanDoctorQrCodeActivity
import com.sumian.sleepdoctor.improve.doctor.base.BasePagerFragment
import com.sumian.sleepdoctor.improve.doctor.bean.Doctor
import com.sumian.sleepdoctor.improve.doctor.contract.DoctorContract
import com.sumian.sleepdoctor.improve.doctor.presenter.DoctorPresenter
import com.sumian.sleepdoctor.notification.NotificationListActivity
import com.sumian.sleepdoctor.notification.NotificationViewModel
import com.sumian.sleepdoctor.widget.RequestScanQrCodeView
import kotlinx.android.synthetic.main.fragment_tab_doctor.*
import java.util.*

/**
 * Created by jzz
 * on 2018/5/2.
 * desc:
 */
class DoctorFragment : BasePagerFragment<DoctorContract.Presenter>(), RequestScanQrCodeView.OnGrantedCallback, DoctorContract.View,
        SwipeRefreshLayout.OnRefreshListener {

    private val TAG: String = DoctorFragment::class.java.javaClass.simpleName

    private var mIsInit = false
    private var mIsAutoRefresh = false

    override fun getLayoutId(): Int {
        return R.layout.fragment_tab_doctor
    }

    override fun initWidget(root: View?) {
        super.initWidget(root)
        doctor_detail_layout.setOnRefreshListener(this)
        iv_notification.setOnClickListener { NotificationListActivity.launch(activity) }
    }

    override fun initData() {
        super.initData()

        mIsInit = true

        if (AppManager.getAccountViewModel()?.userProfile?.isBindDoctor!!) {
            request_scan_qr_code_view.hide()

            val doctor = AppManager.getAccountViewModel()?.userProfile?.doctor
            doctor_detail_layout.invalidDoctor(doctor)
            if (doctor?.services == null) {
                mPresenter.getBindDoctorInfo()
            }
        } else {
            doctor_detail_layout.hide()
            request_scan_qr_code_view.setFragment(this).setOnGrantedCallback(this).show()
        }
        ViewModelProviders.of(Objects.requireNonNull<FragmentActivity>(activity))
                .get(NotificationViewModel::class.java)
                .unreadCount
                .observe(this, Observer { count -> iv_notification.isActivated = count != null && count > 0 })

        AppManager.getDoctorViewModel().getDoctorLiveData().observe(this, Observer { doctor ->
            run {
                if (doctor == null) {
                    doctor_detail_layout.hide()
                    request_scan_qr_code_view.setFragment(this).setOnGrantedCallback(this).show()
                } else
                    onGetDoctorInfoSuccess(doctor)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (!mIsInit) {
            if (mIsAutoRefresh) return
            onRefresh()
        }
    }

    override fun initPresenter() {
        super.initPresenter()
        DoctorPresenter.init(this)
    }

    override fun onRelease() {
        super.onRelease()
        AppManager.getDoctorViewModel().getDoctorLiveData().removeObservers(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        request_scan_qr_code_view.onRequestPermissionsResultDelegate(requestCode, permissions, grantResults)
    }

    override fun onBegin() {
        super.onBegin()
        doctor_detail_layout.showRefreshAnim()
    }

    override fun onFinish() {
        super.onFinish()
        mIsInit = false
        mIsAutoRefresh = false
        doctor_detail_layout.hideRefreshAnim()
    }

    override fun onGrantedSuccess() {
        ScanDoctorQrCodeActivity.show(context!!, ScanDoctorQrCodeActivity::class.java)
    }

    override fun onGetDoctorInfoSuccess(doctor: Doctor?) {
        doctor?.let {
            doctor_detail_layout.invalidDoctor(doctor)
            request_scan_qr_code_view.setFragment(null).hide()
        }
    }

    override fun onNotBindDoctor() {
        doctor_detail_layout.hide()
        request_scan_qr_code_view.setFragment(this).setOnGrantedCallback(this).show()
    }

    override fun onGetDoctorInfoFailed(error: String) {
        showCenterToast(error)
    }

    override fun setPresenter(presenter: DoctorContract.Presenter?) {
        this.mPresenter = presenter
    }

    override fun onRefresh() {
        mIsAutoRefresh = true
        mPresenter.getBindDoctorInfo()
    }

    override fun selectTab(position: Int) {
        if (mIsAutoRefresh) return
        onRefresh()
    }

}
