package com.sumian.sd.service.diary

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.sumian.common.base.BaseFragment
import com.sumian.hw.network.callback.BaseResponseCallback
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.service.diary.bean.DiaryEvaluationsResponse
import kotlinx.android.synthetic.main.fragment_diary_evaluation_list.*

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/9/14 9:15
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class DiaryEvaluationListFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {


    private val mAdapter by lazy {
        DiaryEvaluationListAdapter()
    }
    private var mPage = 1
    private val type: Int
        get() {
            return arguments?.getInt(KEY_TYPE) ?: TYPE_UNFINISHED
        }

    companion object {
        const val TYPE_UNFINISHED = 0
        const val TYPE_FINISHED = 1
        const val PAGE_SIZE = 10

        private const val KEY_TYPE = "KEY_TYPE"

        fun newInstance(type: Int): DiaryEvaluationListFragment {
            val fragment = DiaryEvaluationListFragment()
            val bundle = Bundle()
            bundle.putInt(KEY_TYPE, type)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_diary_evaluation_list
    }

    override fun initWidget() {
        super.initWidget()
        refresh.setOnRefreshListener(this)
        recycler.adapter = mAdapter
        recycler.layoutManager = LinearLayoutManager(activity)
        mAdapter.setOnLoadMoreListener(this, recycler)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            ToastUtils.showShort("todo 启动详情页")
        }
    }

    override fun initData() {
        super.initData()
        refresh.showRefreshAnim()
        queryData(true)
    }

    override fun onRefresh() {
        queryData(true)
    }

    private fun queryData(isInitData: Boolean) {
        if (isInitData) {
            mPage = 1
        }
        val call = AppManager.getHttpService().getDiaryEvaluations(type, null, mPage, PAGE_SIZE)
        call.enqueue(object : BaseResponseCallback<DiaryEvaluationsResponse>() {
            override fun onSuccess(response: DiaryEvaluationsResponse?) {
                LogUtils.d(response)
                val data = response?.data
                if (isInitData) {
                    mAdapter.setNewData(data)
                    mAdapter.setEnableLoadMore(true)
                    empty_error_view.visibility = if (data == null || data.isEmpty()) View.VISIBLE else View.GONE
                } else {
                    if (data != null) {
                        mAdapter.addData(data)
                    }
                    if (response != null) {
                        val pagination = response.meta.pagination
                        if (pagination.currentPage == pagination.totalPages) {
                            mAdapter.loadMoreEnd()
                        } else {
                            mAdapter.loadMoreComplete()
                        }
                    }
                }
                mPage++
            }

            override fun onFailure(code: Int, message: String?) {
                LogUtils.d(message)
            }

            override fun onFinish() {
                super.onFinish()
                refresh.hideRefreshAnim()
            }
        })
    }

    override fun onLoadMoreRequested() {
        queryData(false)
    }
}