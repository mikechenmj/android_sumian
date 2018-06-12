package com.sumian.sleepdoctor.improve.advisory.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.base.BaseActivity
import com.sumian.sleepdoctor.improve.advisory.adapter.RecordAdapter
import com.sumian.sleepdoctor.improve.advisory.bean.Advisory
import com.sumian.sleepdoctor.improve.advisory.contract.RecordContract
import com.sumian.sleepdoctor.improve.advisory.presenter.RecordPresenter
import com.sumian.sleepdoctor.widget.TitleBar
import kotlinx.android.synthetic.main.activity_main_advisory_detail.*

/**
 *
 *Created by sm
 * on 2018/6/4 18:28
 * desc:咨询详情,包含了提问或者回复的记录列表,在线报告列表
 **/
class AdvisoryDetailActivity : BaseActivity<RecordContract.Presenter>(), RecordContract.View, SwipeRefreshLayout.OnRefreshListener, TitleBar.OnBackClickListener, TitleBar.OnMenuClickListener, View.OnClickListener {

    companion object {
        private const val ARGS_ADVISORY_ID = "com.sumian.app.extras.advisory.id"

        fun launch(context: Context, advisoryId: Int) {
            show(context, getLaunchIntent(context, advisoryId))
        }

        fun getLaunchIntent(context: Context, advisoryId: Int): Intent {
            val intent = Intent(context, AdvisoryDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putInt(ARGS_ADVISORY_ID, advisoryId)
            intent.putExtras(bundle)
            return intent
        }
    }

    private var mAdvisoryId: Int = 0
    private lateinit var mAdapter: RecordAdapter

    private lateinit var mAdvisory: Advisory


    override fun initBundle(bundle: Bundle?): Boolean {
        this.mAdvisoryId = bundle?.getInt(ARGS_ADVISORY_ID, 0)!!
        return super.initBundle(bundle)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main_advisory_detail
    }

    override fun initPresenter() {
        super.initPresenter()
        RecordPresenter.init(this)
    }

    override fun initWidget(root: View?) {
        super.initWidget(root)
        title_bar.setOnBackClickListener(this)
        title_bar.setOnMenuClickListener(this)
        refresh.setOnRefreshListener(this)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.itemAnimator = DefaultItemAnimator()
        this.mAdapter = RecordAdapter(this)
        recycler.adapter = mAdapter
        tv_bottom_notification.setOnClickListener(this)
    }

    override fun initData() {
        super.initData()
        mPresenter.getAdvisoryDetail(mAdvisoryId)
    }

    override fun onBegin() {
        super.onBegin()
        refresh.showRefreshAnim()
    }

    override fun onFinish() {
        super.onFinish()
        refresh.hideRefreshAnim()
    }


    @SuppressLint("SetTextI18n")
    override fun onGetAdvisoryDetailSuccess(advisory: Advisory) {
        this.mAdvisory = advisory
        tv_top_notification.text = advisory.remind_description
        tv_top_notification.visibility = View.VISIBLE
        tv_bottom_notification.text = "追问 (剩余${advisory.last_count}机会)"
        tv_bottom_notification.visibility = View.VISIBLE
        this.mAdapter.setDoctor(advisory.doctor!!)
        this.mAdapter.setUser(advisory.user!!)
        this.mAdapter.resetItem(advisory.records)
    }

    override fun onGetAdvisoryDetailFailed(error: String) {
        showCenterToast(error)
    }

    override fun setPresenter(presenter: RecordContract.Presenter?) {
        this.mPresenter = presenter
    }

    override fun onRefresh() {
        mPresenter.getAdvisoryDetail(mAdvisoryId)
    }

    override fun onClick(v: View?) {
        PublishAdvisoryRecordActivity.launch(this, mAdvisory)
    }

    override fun onMenuClick(v: View?) {
        showCenterToast("问题详情FAQ")
    }

    override fun onBack(v: View?) {
        finish()
    }

}