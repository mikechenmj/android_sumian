package com.sumian.sd.buz.tab

import android.graphics.Color
import android.view.View
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseViewModelFragment
import com.sumian.common.utils.ColorCompatUtil
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.doctor.activity.ScanDoctorQrCodeActivity
import com.sumian.sd.buz.doctor.bean.Doctor
import com.sumian.sd.buz.doctor.presenter.DoctorPresenter
import com.sumian.sd.buz.kefu.KefuManager
import com.sumian.sd.buz.notification.NotificationListActivity
import com.sumian.sd.main.OnEnterListener
import com.sumian.sd.widget.RequestScanQrCodeView
import kotlinx.android.synthetic.main.fragment_tab_doctor.*

/**
 * Created by jzz
 * on 2018/5/2.
 * desc:
 */
class DoctorFragment : BaseViewModelFragment<DoctorPresenter>(), RequestScanQrCodeView.OnGrantedCallback, SwipeRefreshLayout.OnRefreshListener, OnEnterListener {

    private var mIsInit = false
    private var mIsAutoRefresh = false

    override fun getLayoutId(): Int {
        return R.layout.fragment_tab_doctor
    }

    override fun initWidget() {
        super.initWidget()
        DoctorPresenter.init(this)
        doctor_detail_layout?.setOnRefreshListener(this)
        iv_notification?.setOnClickListener { NotificationListActivity.launch(activity!!) }
        KefuManager.mMessageCountLiveData.observe(this, Observer {
            showMessageDot(it > 0)
        })
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
                mViewModel?.getBindDoctorInfo()
            }
        }
        request_scan_qr_code_view?.setFragment(this)?.setOnGrantedCallback(this)
        AppManager.getDoctorViewModel().getDoctorLiveData().observe(this, Observer { doctor ->
            run {
                switchUI(doctor != null)
                if (doctor != null) {
                    onGetDoctorInfoSuccess(doctor)
                }
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

    override fun onRelease() {
        super.onRelease()
        AppManager.getDoctorViewModel().getDoctorLiveData().removeObservers(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        request_scan_qr_code_view?.onRequestPermissionsResultDelegate(requestCode, permissions, grantResults)
    }

    fun onBegin() {
        //doctor_detail_layout.showRefreshAnim()
        doctor_detail_layout?.hideRefreshAnim()
    }

    fun onFinish() {
        mIsInit = false
        mIsAutoRefresh = false
        doctor_detail_layout?.hideRefreshAnim()
    }

    override fun onGrantedSuccess() {
        ActivityUtils.startActivity(ScanDoctorQrCodeActivity::class.java)
    }

    fun onGetDoctorInfoSuccess(doctor: Doctor?) {
        doctor?.let {
            lay_doctor_title_container?.visibility = View.VISIBLE
            doctor_detail_layout?.invalidDoctor(doctor)
            request_scan_qr_code_view?.setFragment(null)?.hide()
        }
    }

    fun onNotBindDoctor() {
        switchUI(false)
    }

    fun onGetDoctorInfoFailed(error: String) {
        ToastUtils.showShort(error)
    }

    fun setPresenter(presenter: DoctorPresenter?) {
        this.mViewModel = presenter
    }

    override fun onRefresh() {
        mIsAutoRefresh = true
        mViewModel?.getBindDoctorInfo()
    }

    override fun onEnter(data: String?) {
        if (mIsAutoRefresh) return
        onRefresh()
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
