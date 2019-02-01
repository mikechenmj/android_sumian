package com.sumian.sddoctor.service.evaluation.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.sumian.common.base.BasePresenterFragment
import com.sumian.sddoctor.R
import com.sumian.sddoctor.service.evaluation.activity.WeekEvaluationDetailWebActivity
import com.sumian.sddoctor.service.evaluation.adapter.EvaluationListAdapter
import com.sumian.sddoctor.service.evaluation.bean.WeekEvaluation
import com.sumian.sddoctor.service.evaluation.contract.WeekEvaluationListContract
import com.sumian.sddoctor.service.evaluation.presenter.WeekEvaluationListPresenter
import com.sumian.sddoctor.widget.LoadMoreRecyclerView
import com.sumian.sddoctor.widget.adapter.BaseRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_main_advisory_list.*

/**
 *
 *Created by sm
 * on 2018/6/4 17:32
 * desc:睡眠日记周评估列表
 **/
class WeekEvaluationListFragment : BasePresenterFragment<WeekEvaluationListContract.Presenter>(), WeekEvaluationListContract.View, SwipeRefreshLayout.OnRefreshListener,
        LoadMoreRecyclerView.OnLoadCallback, BaseRecyclerAdapter.OnItemClickListener {

    companion object {

        private const val ARGS_EVALUATION_TYPE: String = "com.sumian.sddoctor.extras.evaluation.type"

        fun newInstance(advisoryType: String = WeekEvaluation.ALL_TYPE): Fragment {
            val args = Bundle()
            args.putString(ARGS_EVALUATION_TYPE, advisoryType)
            val fragment = WeekEvaluationListFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val mEvaluationAdapter: EvaluationListAdapter  by lazy {
        val evaluationListAdapter = EvaluationListAdapter(context!!)
        evaluationListAdapter.setOnItemClickListener(this)
        evaluationListAdapter
    }

    private var mEvaluationType: String = WeekEvaluation.ALL_TYPE

    private var mIsInit = true

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        this.mEvaluationType = bundle.getString(ARGS_EVALUATION_TYPE, WeekEvaluation.ALL_TYPE)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_main_advisory_list
    }

    override fun onInitWidgetBefore() {
        super.onInitWidgetBefore()
        this.mPresenter = WeekEvaluationListPresenter.init(this)
    }

    override fun initWidget() {
        super.initWidget()
        refresh.setOnRefreshListener(this)
        recycler.itemAnimator = null
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = mEvaluationAdapter
        recycler.setOnLoadCallback(this)
        empty_error_view.invalidAdvisoryError()
        empty_error_view.isEnabled = false
    }

    override fun initData() {
        super.initData()
        if (mIsInit) {
            this.mPresenter?.getEvaluationList(mEvaluationType)
        }
    }

    override fun onRefresh() {
        refresh.showRefreshAnim()
        this.mPresenter?.refreshEvaluationList()
        mEvaluationAdapter.setState(BaseRecyclerAdapter.STATE_LOADING, true)
    }

    override fun loadMore() {
        super.loadMore()
        mPresenter?.getNextEvaluationList()
    }

    override fun onItemClick(position: Int, itemId: Long) {
        val weekEvaluation = mEvaluationAdapter.getItem(position)
        WeekEvaluationDetailWebActivity.launch(context!!, weekEvaluation.id)
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

    override fun onGetEvaluationListSuccess(advisories: List<WeekEvaluation>) {
        if (advisories.isEmpty()) {
            empty_error_view.invalidAdvisoryError()
            recycler.visibility = View.GONE
        } else {
            mEvaluationAdapter.addAll(advisories)
            recycler.visibility = View.VISIBLE
            empty_error_view.hide()
        }
    }

    override fun onGetEvaluationListFailed(error: String) {
    }

    override fun onRefreshEvaluationListSuccess(advisories: List<WeekEvaluation>) {
        if (advisories.isEmpty()) {
            empty_error_view.invalidAdvisoryError()
        } else {
            mEvaluationAdapter.resetItem(advisories)
            recycler.visibility = View.VISIBLE
            empty_error_view.hide()
        }
    }

    override fun onHaveMore(isHaveMore: Boolean) {
        mEvaluationAdapter.setState(if (isHaveMore) BaseRecyclerAdapter.STATE_LOAD_MORE else BaseRecyclerAdapter.STATE_NO_MORE, true)
    }

}