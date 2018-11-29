package com.sumian.sd.service.advisory.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.sumian.common.base.BaseRecyclerAdapter
import com.sumian.common.widget.recycler.LoadMoreRecyclerView
import com.sumian.sd.R
import com.sumian.sd.base.SdBaseFragment
import com.sumian.sd.service.advisory.activity.AdvisoryDetailActivity
import com.sumian.sd.service.advisory.activity.PublishAdvisoryRecordActivity
import com.sumian.sd.service.advisory.adapter.AdvisoryListAdapter
import com.sumian.sd.service.advisory.bean.Advisory
import com.sumian.sd.service.advisory.contract.AdvisoryListContract
import com.sumian.sd.service.advisory.presenter.AdvisoryListPresenter
import kotlinx.android.synthetic.main.fragment_main_advisory_list.*

/**
 *
 *Created by sm
 * on 2018/6/4 17:32
 * desc:
 **/
class AdvisoryListFragment : SdBaseFragment<AdvisoryListPresenter>(), AdvisoryListContract.View, androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener, BaseRecyclerAdapter.OnItemClickListener, LoadMoreRecyclerView.OnLoadCallback {

    companion object {

        private const val ARGS_ADVISORY_TYPE: String = "com.sumian.app.extras.advisory.type"

        fun newInstance(advisoryType: Int = Advisory.UNFINISHED_TYPE): androidx.fragment.app.Fragment? {
            val args = Bundle()
            args.putInt(ARGS_ADVISORY_TYPE, advisoryType)
            return SdBaseFragment.newInstance(AdvisoryListFragment::class.java, args)
        }
    }

    private lateinit var mListAdapter: AdvisoryListAdapter

    private var mAdvisoryType: Int = Advisory.UNFINISHED_TYPE

    override fun initBundle(bundle: Bundle?) {
        super.initBundle(bundle)
        this.mAdvisoryType = bundle?.getInt(ARGS_ADVISORY_TYPE, Advisory.UNFINISHED_TYPE)!!
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
        recycler.setOnLoadCallback(this)
        mListAdapter.setOnItemClickListener(this)
        empty_error_view.invalidAdvisoryError()
        empty_error_view.isEnabled = false
    }

    override fun initData() {
        super.initData()
        this.mPresenter.getAdvisories(mAdvisoryType)
    }

    override fun setPresenter(presenter: AdvisoryListContract.Presenter?) {
        this.mPresenter = presenter as AdvisoryListPresenter
    }

    override fun onRefresh() {
        this.mPresenter.refreshAdvisories()
        refresh.showRefreshAnim()
    }

    override fun loadMore() {
        super.loadMore()
        mPresenter?.getNextAdvisories()
    }

    override fun onItemClick(position: Int, itemId: Long) {
        val advisory = mListAdapter.getItem(position)
        when (advisory.status) {
            5 -> {
                PublishAdvisoryRecordActivity.show(context!!, advisory.id)
            }
            else -> {
                AdvisoryDetailActivity.show(context!!, advisory.id)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        this.mPresenter.refreshAdvisories()
        refresh.showRefreshAnim()
    }

    override fun onBegin() {
        super.onBegin()
    }

    override fun onFinish() {
        super.onFinish()
        refresh.hideRefreshAnim()
    }

    override fun onGetAdvisoriesSuccess(advisories: List<Advisory>) {
        if (advisories.isEmpty()) {
            empty_error_view.invalidAdvisoryError()
            recycler.visibility = View.GONE
        } else {
            mListAdapter.resetItem(advisories)
            recycler.visibility = View.VISIBLE
            empty_error_view.hide()
        }
    }

    override fun onGetAdvisoriesFailed(error: String) {
        showCenterToast(error)
    }

    override fun onGetNextAdvisoriesSuccess(advisories: List<Advisory>) {
        if (advisories.isEmpty()) return
        mListAdapter.addAll(advisories)
        recycler.visibility = View.VISIBLE
        empty_error_view.hide()
    }

    override fun onRefreshAdvisoriesSuccess(advisories: List<Advisory>) {
        mListAdapter.clear()
        if (advisories.isEmpty()) {
            empty_error_view.invalidAdvisoryError()
        } else {
            mListAdapter.resetItem(advisories)
            recycler.visibility = View.VISIBLE
            empty_error_view.hide()
        }
    }

}