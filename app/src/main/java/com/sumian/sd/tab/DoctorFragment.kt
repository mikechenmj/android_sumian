package com.sumian.sd.tab

import android.graphics.Color
import android.view.View
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.sumian.common.utils.ColorCompatUtil
import com.sumian.hw.leancloud.HwLeanCloudHelper
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.base.SdBaseFragment
import com.sumian.sd.doctor.activity.ScanDoctorQrCodeActivity
import com.sumian.sd.doctor.bean.Doctor
import com.sumian.sd.doctor.contract.DoctorContract
import com.sumian.sd.doctor.presenter.DoctorPresenter
import com.sumian.sd.main.OnEnterListener
import com.sumian.sd.notification.NotificationListActivity
import com.sumian.sd.widget.RequestScanQrCodeView
import kotlinx.android.synthetic.main.fragment_tab_doctor.*

/**
 * Created by jzz
 * on 2018/5/2.
 * desc:
 */
class DoctorFragment : SdBaseFragment<DoctorContract.Presenter>(), RequestScanQrCodeView.OnGrantedCallback, DoctorContract.View, SwipeRefreshLayout.OnRefreshListener, OnEnterListener, HwLeanCloudHelper.OnShowMsgDotCallback {

    private var mIsInit = false
    private var mIsAutoRefresh = false

    override fun getLayoutId(): Int {
        return R.layout.fragment_tab_doctor
    }

    override fun initWidget(root: View?) {
        super.initWidget(root)
        doctor_detail_layout?.setOnRefreshListener(this)
        iv_notification?.setOnClickListener { NotificationListActivity.launch(activity) }
    }

    override fun initData() {
        super.initData()

        mIsInit = true

        val isBindDoctor = AppManager.getAccountViewModel().userInfo?.isBindDoctor!!
        switchUI(isBindDoctor)
        if (isBindDoctor) {
            request_scan_qr_code_view?.hide()
            val doctor = AppManager.getAccountViewModel().userInfo?.doctor
            doctor?.let {
                doctor_detail_layout?.invalidDoctor(doctor)
            }
            if (doctor?.services == null) {
                mIsAutoRefresh = true
                mPresenter.getBindDoctorInfo()
            }
        }
        request_scan_qr_code_view?.setFragment(this)?.setOnGrantedCallback(this)
        //去掉医生模块的通知提醒
//        ViewModelProviders.of(Objects.requireNonNull<FragmentActivity>(activity))
//                .get(NotificationViewModel::class.java)
//                .unreadCount
//                .observe(this, Observer { count -> iv_notification.isActivated = count != null && count > 0 })
        AppManager.getDoctorViewModel().getDoctorLiveData().observe(this, Observer { doctor ->
            run {
                switchUI(doctor != null)
                if (doctor != null) {
                    onGetDoctorInfoSuccess(doctor)
                }
            }
        })

        HwLeanCloudHelper.addOnAdminMsgCallback(this)
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
        request_scan_qr_code_view?.onRequestPermissionsResultDelegate(requestCode, permissions, grantResults)
    }

    override fun onBegin() {
        super.onBegin()
        //doctor_detail_layout.showRefreshAnim()
        doctor_detail_layout?.hideRefreshAnim()
    }

    override fun onFinish() {
        super.onFinish()
        mIsInit = false
        mIsAutoRefresh = false
        doctor_detail_layout?.hideRefreshAnim()
    }

    override fun onGrantedSuccess() {
        ScanDoctorQrCodeActivity.show(context!!, ScanDoctorQrCodeActivity::class.java)
    }

    override fun onGetDoctorInfoSuccess(doctor: Doctor?) {
        doctor?.let {
            lay_doctor_title_container?.visibility = View.VISIBLE
            doctor_detail_layout?.invalidDoctor(doctor)
            request_scan_qr_code_view?.setFragment(null)?.hide()
        }
    }

    override fun onNotBindDoctor() {
        switchUI(false)
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

    override fun onEnter(data: String?) {
        runOnUiThread {
            val isHaveMsg = HwLeanCloudHelper.isHaveCustomerMsg()
            showMessageDot(isHaveMsg)
        }
        if (mIsAutoRefresh) return
        onRefresh()
    }

    override fun onShowMsgDotCallback(adminMsgLen: Int, doctorMsgLen: Int, customerMsgLen: Int) {
        onHideMsgCallback(adminMsgLen, doctorMsgLen, customerMsgLen)
    }

    override fun onHideMsgCallback(adminMsgLen: Int, doctorMsgLen: Int, customerMsgLen: Int) {
        runOnUiThread { showMessageDot(customerMsgLen > 0) }
    }

    private fun showMessageDot(isHaveMsg: Boolean) {
        doctor_detail_layout?.showMsgDot(isHaveMsg)
        request_scan_qr_code_view?.showMsgDot(isHaveMsg)
    }

    private fun switchUI(hasDoctor: Boolean) {
        doctor_detail_layout?.visibility = if (hasDoctor) View.VISIBLE else View.GONE
        request_scan_qr_code_view?.visibility = if (!hasDoctor) View.VISIBLE else View.GONE
        tv_title?.visibility = if (hasDoctor) View.VISIBLE else View.GONE
        iv_notification?.setImageResource(if (hasDoctor) R.drawable.sel_notification else R.drawable.sel_notification_black)
        lay_doctor_title_container?.setBackgroundColor(if (hasDoctor) ColorCompatUtil.getColor(activity!!, R.color.colorPrimary) else Color.TRANSPARENT)
    }
}
