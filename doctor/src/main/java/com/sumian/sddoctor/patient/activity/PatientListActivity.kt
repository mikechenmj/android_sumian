package com.sumian.sddoctor.patient.activity

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.sddoctor.R
import com.sumian.sddoctor.base.SddBaseViewModelActivity
import com.sumian.sddoctor.booking.DoctorQrCodeActivity
import com.sumian.sddoctor.constants.StatConstants
import com.sumian.sddoctor.patient.adapter.PatientAdapter
import com.sumian.sddoctor.patient.bean.Patient
import com.sumian.sddoctor.patient.contract.PatientListContract
import com.sumian.sddoctor.patient.presenter.PatientListPresenter
import com.sumian.sddoctor.patient.widget.PatientEmptyView
import com.sumian.sddoctor.widget.LoadMoreRecyclerView
import com.sumian.sddoctor.widget.adapter.BaseRecyclerAdapter
import kotlinx.android.synthetic.main.activity_main_patient_list.*

/**
 * Created by sm
 *
 * on 2018/8/29
 *
 * desc:获取绑定的患者列表   默认是按绑定的时间排序
 *
 */
class PatientListActivity : SddBaseViewModelActivity<PatientListPresenter>(),
        SwipeRefreshLayout.OnRefreshListener, LoadMoreRecyclerView.OnLoadCallback,
        PatientEmptyView.OnEmptyPatientCallback, BaseRecyclerAdapter.OnItemClickListener, PatientListContract.View {

    companion object {

        @JvmStatic
        fun show() {
            ActivityUtils.getTopActivity()?.let {
                it.startActivity(Intent(it, PatientListActivity::class.java))
            }
        }

    }

    private val mPatientAdapter: PatientAdapter  by lazy {
        val patientAdapter = PatientAdapter(this)
        patientAdapter.setOnItemClickListener(this)
        return@lazy patientAdapter
    }

    override fun showBackNav(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main_patient_list
    }

    override fun getPageName(): String {
        return StatConstants.page_service_new_list
    }

    override fun initWidgetBefore() {
        super.initWidgetBefore()
        this.mViewModel = PatientListPresenter.init(this)
    }

    override fun initWidget() {
        super.initWidget()
        mTitleBar.setTitle(R.string.new_patient_list)

        recycler?.adapter = mPatientAdapter
        recycler?.layoutManager = LinearLayoutManager(this)
        recycler?.itemAnimator = DefaultItemAnimator()
        recycler?.setOnLoadCallback(this)
        swipe_refresh?.setOnRefreshListener(this)
    }

    override fun onResume() {
        super.onResume()
        mViewModel?.refreshPatients()
    }

    override fun showLoading() {
        //super.showLoading()
    }

    override fun dismissLoading() {
        //super.dismissLoading()
        swipe_refresh?.hideRefreshAnim()
    }

    override fun onAddPatientCallback() {
        ActivityUtils.startActivity(DoctorQrCodeActivity::class.java)
    }

    override fun onItemClick(position: Int, itemId: Long) {
        val item = mPatientAdapter.getItem(position)!!
        PatientInfoActivity.show(this@PatientListActivity, item.id, item.consulted)
    }

    override fun loadMorePatientsSuccess(patients: ArrayList<Patient>?) {
//        updatePatients(patients)
        mPatientAdapter.addAll(patients)
        mPatientAdapter.notifyDataSetChanged()
    }

    override fun getPatientFailed(error: String) {
        ToastUtils.showShort(error)
    }

    override fun onRefresh() {
        swipe_refresh?.showRefreshAnim()
        this.mViewModel?.refreshPatients()
    }

    override fun loadMore() {
        super.loadMore()
        mViewModel?.getNextPatients()
    }

    override fun onHaveMore(isHaveMore: Boolean) {
        mPatientAdapter.setState(if (isHaveMore) BaseRecyclerAdapter.STATE_LOAD_MORE else BaseRecyclerAdapter.STATE_NO_MORE, true)
    }

    override fun onRefreshPatientsSuccess(patients: ArrayList<Patient>?) {
        updatePatients(patients)
    }

    private fun updatePatients(patients: ArrayList<Patient>?) {
        if (patients == null || patients.isEmpty()) {
            recycler.visibility = View.GONE
            patient_list_empty_view?.show()
        } else {
            mPatientAdapter.resetItem(patients)
            recycler.visibility = View.VISIBLE
            patient_list_empty_view?.hide()
        }
    }
}