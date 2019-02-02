package com.sumian.sddoctor.service.advisory.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.sumian.common.base.BaseViewModelFragment
import com.sumian.sddoctor.R
import com.sumian.sddoctor.service.advisory.activity.AdvisoryDetailActivity
import com.sumian.sddoctor.service.advisory.adapter.AdvisoryListAdapter
import com.sumian.sddoctor.service.advisory.bean.Advisory
import com.sumian.sddoctor.service.advisory.contract.AdvisoryListContract
import com.sumian.sddoctor.service.advisory.presenter.AdvisoryListPresenter
import com.sumian.sddoctor.widget.LoadMoreRecyclerView
import com.sumian.sddoctor.widget.adapter.BaseRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_main_advisory_list.*

/**
 *
 *Created by sm
 * on 2018/6/4 17:32
 * desc:图文咨询列表
 **/
class AdvisoryListFragment : BaseViewModelFragment<AdvisoryListPresenter>(), AdvisoryListContract.View, SwipeRefreshLayout.OnRefreshListener,
        LoadMoreRecyclerView.OnLoadCallback, BaseRecyclerAdapter.OnItemClickListener {

    companion object {

        private const val ARGS_ADVISORY_TYPE: String = "com.sumian.sddoctor.extras.advisory.type"

        fun newInstance(advisoryType: Int = Advisory.ALL_TYPE): Fragment {
            val args = Bundle()
            args.putInt(ARGS_ADVISORY_TYPE, advisoryType)
            val fragment = AdvisoryListFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var advisoryListAdapter: AdvisoryListAdapter

    private var mAdvisoryType: Int = Advisory.ALL_TYPE

    private var mIsInit = true

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        this.mAdvisoryType = bundle.getInt(ARGS_ADVISORY_TYPE, Advisory.ALL_TYPE)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_main_advisory_list
    }

    override fun onInitWidgetBefore() {
        super.onInitWidgetBefore()
        this.mViewModel = AdvisoryListPresenter.init(this)
    }

    override fun initWidget() {
        super.initWidget()
        refresh.setOnRefreshListener(this)
        recycler.layoutManager = LinearLayoutManager(context)
        advisoryListAdapter = AdvisoryListAdapter(context!!)
        recycler.adapter = advisoryListAdapter
        recycler.setOnLoadCallback(this)
        advisoryListAdapter.setOnItemClickListener(this)
        empty_error_view.invalidAdvisoryError()
        empty_error_view.isEnabled = false
    }

    override fun initData() {
        super.initData()
        if (mIsInit) {
            this.mViewModel?.getAdvisories(mAdvisoryType)
        }
    }

    override fun onRefresh() {
        refresh.showRefreshAnim()
        this.mViewModel?.refreshAdvisories()
        advisoryListAdapter.setState(BaseRecyclerAdapter.STATE_LOADING, true)
    }

    override fun loadMore() {
        super.loadMore()
        mViewModel?.getNextAdvisories()
    }

    override fun onItemClick(position: Int, itemId: Long) {
        val advisory = advisoryListAdapter.getItem(position)
        when (advisory.status) {
            5 -> {
                // PublishAdvisoryRecordActivity.launch(context!!, advisory.id)
            }
            else -> {
                AdvisoryDetailActivity.show(advisory.id, advisory.traceable.user_id)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!mIsInit) {
            onRefresh()
        }
    }

    override fun showLoading() {
        //super.showLoading()
    }

    override fun dismissLoading() {
        //super.dismissLoading()
        mIsInit = false
        refresh.hideRefreshAnim()
    }

    override fun onGetAdvisoriesSuccess(advisories: List<Advisory>) {
        if (advisories.isEmpty()) {
            empty_error_view.invalidAdvisoryError()
            recycler.visibility = View.GONE
        } else {
            advisoryListAdapter.addAll(advisories)
            recycler.visibility = View.VISIBLE
            empty_error_view.hide()
        }
    }

    override fun onGetAdvisoriesFailed(error: String) {
    }

    override fun onRefreshAdvisoriesSuccess(advisories: List<Advisory>) {
        if (advisories.isEmpty()) {
            empty_error_view.invalidAdvisoryError()
        } else {
            advisoryListAdapter.resetItem(advisories)
            recycler.visibility = View.VISIBLE
            empty_error_view.hide()
        }
    }

    override fun onHaveMore(isHaveMore: Boolean) {
        advisoryListAdapter.setState(if (isHaveMore) BaseRecyclerAdapter.STATE_LOAD_MORE else BaseRecyclerAdapter.STATE_NO_MORE, true)
    }

}