package com.sumian.sleepdoctor.improve.advisory.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.sumian.common.base.BaseRecyclerAdapter
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.base.BaseFragment
import com.sumian.sleepdoctor.improve.advisory.activity.AdvisoryDetailActivity
import com.sumian.sleepdoctor.improve.advisory.activity.AdvisoryListActivity
import com.sumian.sleepdoctor.improve.advisory.adapter.AdvisoryListAdapter
import com.sumian.sleepdoctor.improve.advisory.bean.Advisory
import com.sumian.sleepdoctor.improve.advisory.contract.AdvisoryListContract
import com.sumian.sleepdoctor.improve.advisory.presenter.AdvisoryListPresenter
import kotlinx.android.synthetic.main.fragment_main_advisory_list.*

/**
 *
 *Created by sm
 * on 2018/6/4 17:32
 * desc:
 **/
class AdvisoryListFragment : BaseFragment<AdvisoryListPresenter>(), AdvisoryListContract.View, SwipeRefreshLayout.OnRefreshListener, BaseRecyclerAdapter.OnItemClickListener {

    companion object {

        private const val ARGS_ADVISORY_TYPE: String = "com.sumian.app.extras.advisory.type"

        fun newInstance(advisoryType: Int = Advisory.UNUSED_TYPE): Fragment? {
            val args = Bundle()
            args.putInt(ARGS_ADVISORY_TYPE, advisoryType)
            return BaseFragment.newInstance(AdvisoryListFragment::class.java, args)
        }
    }

    private lateinit var mListAdapter: AdvisoryListAdapter

    private var mAdvisoryType: Int = Advisory.UNUSED_TYPE

    override fun initBundle(bundle: Bundle?) {
        super.initBundle(bundle)
        this.mAdvisoryType = bundle?.getInt(ARGS_ADVISORY_TYPE, Advisory.UNUSED_TYPE)!!
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_main_advisory_list
    }

    override fun initPresenter() {
        super.initPresenter()
        AdvisoryListPresenter.init(this)
    }

    override fun initWidget(root: View?) {
        super.initWidget(root)
        refresh.setOnRefreshListener(this)
        recycler.layoutManager = LinearLayoutManager(context)
        mListAdapter = AdvisoryListAdapter(context!!)
        recycler.adapter = mListAdapter
        mListAdapter.setOnItemClickListener(this)
        empty_error_view.invalidAdvisoryError()
    }

    override fun initData() {
        super.initData()
        this.mPresenter.getAdvisories(mAdvisoryType, (this.mActivity as AdvisoryListActivity).getAdvisoryId())
    }

    override fun setPresenter(presenter: AdvisoryListContract.Presenter?) {
        this.mPresenter = presenter as AdvisoryListPresenter
    }

    override fun onRefresh() {
        this.mPresenter.refreshAdvisories()
    }

    override fun onItemClick(position: Int, itemId: Long) {
        if (mAdvisoryType == Advisory.USED_TYPE) {
            val advisory = mListAdapter.getItem(position)
            AdvisoryDetailActivity.launch(context!!, advisory.id)
        } else {
            showCenterToast("去填写图文咨询报告")
        }
    }

    override fun onBegin() {
        super.onBegin()
        refresh.showRefreshAnim()
    }

    override fun onFinish() {
        super.onFinish()
        refresh.hideRefreshAnim()
    }

    override fun onGetAdvisoriesSuccess(advisories: ArrayList<Advisory>) {
        if (advisories.isEmpty()) {
            empty_error_view.invalidAdvisoryError()
            recycler.visibility = View.GONE
        } else {
            mListAdapter.addAll(advisories)
            recycler.visibility = View.VISIBLE
            empty_error_view.hide()
        }
    }

    override fun onGetAdvisoriesFailed(error: String) {
        showCenterToast(error)
    }

    override fun onGetNextAdvisoriesSuccess(advisories: ArrayList<Advisory>) {
        if (advisories.isEmpty()) return
        mListAdapter.addAll(advisories)
        recycler.visibility = View.VISIBLE
        empty_error_view.hide()
    }

    override fun onRefreshAdvisoriesSuccess(advisories: ArrayList<Advisory>) {
        if (advisories.isEmpty()) return
        mListAdapter.resetItem(advisories)
        recycler.visibility = View.VISIBLE
        empty_error_view.hide()
    }

}