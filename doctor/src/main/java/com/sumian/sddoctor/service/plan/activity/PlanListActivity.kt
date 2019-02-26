package com.sumian.sddoctor.service.plan.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.sumian.sddoctor.R
import com.sumian.sddoctor.base.SddBaseViewModelActivity
import com.sumian.sddoctor.constants.StatConstants
import com.sumian.sddoctor.service.plan.adapter.PlanListAdapter
import com.sumian.sddoctor.service.plan.bean.Plan
import com.sumian.sddoctor.service.plan.contract.PlanContract
import com.sumian.sddoctor.service.plan.presenter.PlanPresenter
import com.sumian.sddoctor.widget.EmptyErrorView
import kotlinx.android.synthetic.main.activity_main_scale_list.*

/**
 * Created by  dq
 *
 * on 2018/08/30
 *
 * desc:  随访计划选择列表
 */
class PlanListActivity : SddBaseViewModelActivity<PlanPresenter>(), BaseQuickAdapter.OnItemClickListener, View.OnClickListener, PlanContract.View {

    companion object {

        private const val EXTRAS_PATIENT_ID = "com.sumian.sddoctor.extras.patient.id"

        fun show(patientId: Int) {
            val topActivity = ActivityUtils.getTopActivity()
            val intent = Intent(topActivity, PlanListActivity::class.java)
            intent.putExtra(EXTRAS_PATIENT_ID, patientId)
            topActivity.startActivity(intent)
        }
    }

    private val emptyView: View  by lazy {
        EmptyErrorView.create(this, R.mipmap.ic_empty_state_report, R.string.none_have_scale, R.string.no_data_hint)
    }

    private val mAdapter: PlanListAdapter  by lazy {
        val adapter = PlanListAdapter(null)
        adapter.onItemClickListener = this
        adapter.emptyView = emptyView
        adapter.setEnableLoadMore(false)
        adapter.setMaxSelectCount(1)
        return@lazy adapter
    }

    private var mPatientId = 0

    override fun showBackNav(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main_scale_list
    }

    override fun getPageName(): String {
        return StatConstants.page_patient_send_plan
    }

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        this.mPatientId = bundle.getInt(EXTRAS_PATIENT_ID, 0)
    }

    override fun initWidgetBefore() {
        super.initWidgetBefore()
        this.mViewModel = PlanPresenter.init(this)
    }

    override fun initWidget() {
        super.initWidget()
        mTitleBar.setTitle(R.string.send_follow_up_plan)
        tv_send.setOnClickListener(this)

        recycler_view.adapter = mAdapter
        recycler_view.itemAnimator = DefaultItemAnimator()
        recycler_view.layoutManager = LinearLayoutManager(this)
    }

    override fun initData() {
        super.initData()
        mViewModel?.getFollowPlans()
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        // val item = adapter.getItem(position) as Plan? ?: return
        mAdapter.addOrRemoveSelectedItem(position)
    }

    override fun onClick(v: View) {
        mViewModel?.sendFollowPlans(patientId = mPatientId, followPlanId = mAdapter.getSelectedPlanId())
    }

    override fun onSendFollowPlansSuccess(success: String) {
        ToastUtils.showShort(success)
        finish()
    }

    override fun onSendFollowPlansFailed(error: String) {
        ToastUtils.showShort(error)
    }

    override fun onGetFollowPlansSuccess(plans: List<Plan>) {
        mAdapter.replaceData(plans)
    }

    override fun onGetFollowPlansFailed(error: String) {
        ToastUtils.showShort(error)
    }

}
