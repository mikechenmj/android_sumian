package com.sumian.sddoctor.service.scale.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.sumian.sddoctor.R
import com.sumian.sddoctor.base.SddBaseActivity
import com.sumian.sddoctor.service.scale.adapter.ScaleListAdapter
import com.sumian.sddoctor.service.scale.bean.Scale
import com.sumian.sddoctor.service.scale.contract.ScaleContract
import com.sumian.sddoctor.service.scale.presenter.ScalePresenter
import com.sumian.sddoctor.widget.EmptyErrorView
import kotlinx.android.synthetic.main.activity_main_scale_list.*

/**
 * Created by  dq
 *
 * on 2018/08/30
 *
 * desc:  量表选择列表
 */
class ScaleListActivity : SddBaseActivity<ScaleContract.Presenter>(), BaseQuickAdapter.OnItemClickListener, View.OnClickListener, ScaleContract.View {

    companion object {

        private const val EXTRAS_PATIENT_ID = "com.sumian.sddoctor.extras.patient.id"


        fun show(patientId: Int) {
            val topActivity = ActivityUtils.getTopActivity()
            val intent = Intent(topActivity, ScaleListActivity::class.java)
            intent.putExtra(EXTRAS_PATIENT_ID, patientId)
            topActivity.startActivity(intent)
        }
    }

    private val emptyView: View  by lazy {
        EmptyErrorView.create(this, R.mipmap.ic_empty_state_report, R.string.none_have_scale, R.string.no_data_hint)
    }

    private val mAdapter: ScaleListAdapter  by lazy {
        val adapter = ScaleListAdapter(null)
        adapter.onItemClickListener = this
        adapter.emptyView = emptyView
        adapter.setEnableLoadMore(false)
        adapter.isPickMode = true
        return@lazy adapter
    }

    private var mPatientId = 0

    override fun showBackNav(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main_scale_list
    }

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        this.mPatientId = bundle.getInt(EXTRAS_PATIENT_ID, 0)
    }

    override fun initWidgetBefore() {
        super.initWidgetBefore()
        this.mPresenter = ScalePresenter.init(this)
    }

    override fun initWidget() {
        super.initWidget()
        mTitleBar.setTitle(R.string.send_scale)
        tv_send.setOnClickListener(this)

        recycler_view.adapter = mAdapter
        recycler_view.itemAnimator = DefaultItemAnimator()
        recycler_view.layoutManager = LinearLayoutManager(this)
    }

    override fun initData() {
        super.initData()
        mPresenter?.getScales()
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        // val item = adapter.getItem(position) as Plan? ?: return
        mAdapter.addOrRemoveSelectedItem(position)
    }

    override fun onClick(v: View) {
        mPresenter?.sendScale(patientId = mPatientId, scaleIds = mAdapter.selectedScaleIds)
    }

    override fun onSendScaleSuccess(success: String) {
        ToastUtils.showShort(success)
        finish()
    }

    override fun onSendScaleFailed(error: String) {
        ToastUtils.showShort(error)
    }

    override fun onGetScalesSuccess(plans: List<Scale>) {
        mAdapter.replaceData(plans)
    }

    override fun onGetScalesFailed(error: String) {
        ToastUtils.showShort(error)
    }

}
