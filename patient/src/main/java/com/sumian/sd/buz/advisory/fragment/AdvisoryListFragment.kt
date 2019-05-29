package com.sumian.sd.buz.advisory.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseRecyclerAdapter
import com.sumian.common.base.BaseViewModelFragment
import com.sumian.common.widget.recycler.LoadMoreRecyclerView
import com.sumian.sd.R
import com.sumian.sd.buz.advisory.activity.AdvisoryDetailActivity
import com.sumian.sd.buz.advisory.activity.PublishAdvisoryRecordActivity
import com.sumian.sd.buz.advisory.adapter.AdvisoryListAdapter
import com.sumian.sd.buz.advisory.bean.Advisory
import com.sumian.sd.buz.advisory.contract.AdvisoryListContract
import com.sumian.sd.buz.advisory.presenter.AdvisoryListPresenter
import kotlinx.android.synthetic.main.fragment_main_advisory_list.*

/**
 *
 *Created by sm
 * on 2018/6/4 17:32
 * desc:
 **/
class AdvisoryListFragment : BaseViewModelFragment<AdvisoryListPresenter>(), AdvisoryListContract.View, SwipeRefreshLayout.OnRefreshListener, BaseRecyclerAdapter.OnItemClickListener, LoadMoreRecyclerView.OnLoadCallback {

    companion object {

        private const val ARGS_ADVISORY_TYPE: String = "com.sumian.app.extras.advisory.type"

        @JvmStatic
        fun newInstance(advisoryType: Int = Advisory.UNFINISHED_TYPE): Fragment? {
            val args = Bundle()
            args.putInt(ARGS_ADVISORY_TYPE, advisoryType)
            val fragment = AdvisoryListFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var mListAdapter: AdvisoryListAdapter

    private var mAdvisoryType: Int = Advisory.UNFINISHED_TYPE

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        this.mAdvisoryType = bundle.getInt(ARGS_ADVISORY_TYPE, Advisory.UNFINISHED_TYPE)!!
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_main_advisory_list
    }

    override fun initWidget() {
        super.initWidget()
        AdvisoryListPresenter.init(this)
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
        this.mViewModel?.getAdvisories(mAdvisoryType)
    }

    override fun setPresenter(presenter: AdvisoryListPresenter) {
        this.mViewModel = presenter
    }

    override fun onRefresh() {
        this.mViewModel?.refreshAdvisories()
        refresh?.showRefreshAnim()
    }

    override fun loadMore() {
        super.loadMore()
        mViewModel?.getNextAdvisories()
    }

    override fun onItemClick(position: Int, itemId: Long) {
        val advisory = mListAdapter.getItem(position)
        when (advisory.status) {
            5 -> {
                PublishAdvisoryRecordActivity.show(context!!, advisory.id, false)
            }
            else -> {
                AdvisoryDetailActivity.show(context!!, advisory.id)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        this.mViewModel?.refreshAdvisories()
        refresh?.showRefreshAnim()
    }

    override fun onFinish() {
        super.onFinish()
        refresh?.hideRefreshAnim()
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
        ToastUtils.showShort(error)
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