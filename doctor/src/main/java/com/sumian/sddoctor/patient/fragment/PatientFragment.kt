package com.sumian.sddoctor.patient.fragment

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.sddoctor.R
import com.sumian.sddoctor.base.BasePresenterFragment
import com.sumian.sddoctor.booking.DoctorQrCodeActivity
import com.sumian.sddoctor.patient.activity.PatientListActivity
import com.sumian.sddoctor.patient.adapter.GroupAdapter
import com.sumian.sddoctor.patient.adapter.ITrigger
import com.sumian.sddoctor.patient.bean.Group
import com.sumian.sddoctor.patient.contract.PatientGroupContract
import com.sumian.sddoctor.patient.presenter.PatientGroupPresenter
import com.sumian.sddoctor.patient.widget.PatientEmptyView
import com.sumian.sddoctor.patient.widget.PatientSortView
import com.sumian.sddoctor.widget.TitleBar
import kotlinx.android.synthetic.main.fragment_patient.*

/**
 * Created by dq
 *
 * on  2018/08/29
 *
 * desc:  分组患者tab
 */
class PatientFragment : BasePresenterFragment<PatientGroupContract.Presenter>(), TitleBar.OnMenuClickListener,
        PatientEmptyView.OnEmptyPatientCallback, PatientGroupContract.View, SwipeRefreshLayout.OnRefreshListener, TitleBar.OnSpannerListener,
        PatientSortView.OnPatientSortViewCallback, ITrigger {

    private val TAG: String = PatientFragment::class.java.simpleName

    private var mIsReSort = false

    private var mIsInit = false

    private var mPosition = -1

    private val mGroupAdapter: GroupAdapter by lazy {
        val adapter = GroupAdapter().setOnTrigger(this)
        adapter
    }

    override fun initWidget(root: View) {
        super.initWidget(root)

        title_bar?.setOnMenuClickListener(this)
        title_bar?.isShow(false)
        title_bar?.addOnSpannerListener(this)
        patient_empty_view?.setOnEmptyPatientCallback(this)

        recycler?.adapter = mGroupAdapter
        recycler?.layoutManager = LinearLayoutManager(activity)
        //recycler?.itemAnimator = DefaultItemAnimator()
        swipe_refresh?.setOnRefreshListener(this)

        patient_sort_view.setOnPatientSortViewCallback(this)
        lay_show_new_patient_list.setOnClickListener {
            PatientListActivity.show()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_patient
    }

    override fun initPresenter() {
        super.initPresenter()
        this.mPresenter = PatientGroupPresenter.init(this)
    }

    override fun initData() {
        super.initData()
        mIsInit = true
        if (patient_sort_view?.isSortByLevel()!!) {
            this.mPresenter?.getLevelGroupPatients(2, 1, 0)
        } else {
            this.mPresenter?.getFaceGroupPatients(1, 0)
        }
    }

    override fun onResume() {
        super.onResume()
        if (!mIsInit) {
            onRefresh()
        }
    }

    override fun onMenuClick(v: View) {
        onAddPatientCallback()
    }

    override fun showLoading() {
        swipe_refresh?.showRefreshAnim()
    }

    override fun dismissLoading() {
        swipe_refresh?.hideRefreshAnim()
    }

    override fun onRefresh() {
        this.mPresenter?.refreshGroupPatients()
    }

    override fun onAddPatientCallback() {
        ActivityUtils.startActivity(DoctorQrCodeActivity::class.java)
    }

    override fun onGetGroupsSuccess(groups: MutableList<Group>) {
        mIsInit = false
        mGroupAdapter.resetItems(groups)
    }

    override fun onGetGroupsFailed(error: String) {
        if (mIsInit) {
            onShowEmptyView()
        }
        mIsInit = false
        showCenterToast(error)
    }

    override fun onRefreshGroupsSuccess(groups: MutableList<Group>) {
        mGroupAdapter.resetItems(groups)
    }

    override fun onHideEmptyView() {
        patient_empty_view?.hide()
    }

    override fun onShowEmptyView() {
        patient_empty_view?.show()
    }

    override fun onSpanner(v: View, isShow: Boolean) {
        if (isShow) {
            patient_sort_view?.show()
        } else {
            patient_sort_view?.dismiss()
        }
    }

    override fun sortByLevelCallback() {
        Log.e(TAG, "sortByLevelCallback()")
        title_bar?.setTitle(R.string.patient_level)
        mIsReSort = true
        mPresenter?.getLevelGroupPatients(2, 1, 0)
    }

    override fun sortByFaceCallback() {
        Log.e(TAG, "sortByFaceCallback()")
        title_bar?.setTitle(R.string.face_patient)
        mIsReSort = true
        mPresenter?.getFaceGroupPatients(1, 0)
    }

    override fun dismissCallback() {
        title_bar?.isShow(false)
    }

    override fun onTrigger(position: Int, group: Group) {
        mIsReSort = false
        mPosition = position

        if (patient_sort_view?.isSortByLevel()!!) {
            this.mPresenter?.getLevelGroupPatients(2, 1, 0)
        } else {
            this.mPresenter?.getFaceGroupPatients(1, 0)
        }
    }

}
