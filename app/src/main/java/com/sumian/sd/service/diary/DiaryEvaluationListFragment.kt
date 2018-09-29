@file:Suppress("UNUSED_PARAMETER", "unused")

package com.sumian.sd.service.diary

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.blankj.utilcode.util.LogUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.sumian.common.base.BaseFragment
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.event.EventBusUtil
import com.sumian.sd.network.callback.BaseSdResponseCallback
import com.sumian.sd.service.diary.bean.DiaryEvaluationData
import com.sumian.sd.service.diary.bean.DiaryEvaluationsResponse
import com.sumian.sd.widget.SumianLoadMoreView
import kotlinx.android.synthetic.main.fragment_diary_evaluation_list.*
import org.greenrobot.eventbus.Subscribe

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
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

    override fun onStart() {
        super.onStart()
        EventBusUtil.register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBusUtil.unregister(this)
    }

    override fun initWidget() {
        super.initWidget()
        refresh.setOnRefreshListener(this)
        recycler.adapter = mAdapter
        recycler.layoutManager = LinearLayoutManager(activity)
        mAdapter.setOnLoadMoreListener(this, recycler)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            val item = adapter.getItem(position) as DiaryEvaluationData
            DiaryEvaluationDetailActivity.launch(activity, item.id)
        }
        mAdapter.setLoadMoreView(SumianLoadMoreView())
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
        val call = AppManager.getSdHttpService().getDiaryEvaluations(type, null, mPage, PAGE_SIZE)
        call.enqueue(object : BaseSdResponseCallback<DiaryEvaluationsResponse>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                LogUtils.d(errorResponse.message)
            }

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
                        if (pagination.currentPage >= pagination.totalPages) {
                            mAdapter.loadMoreEnd()
                        } else {
                            mAdapter.loadMoreComplete()
                        }
                    }
                }
                mPage++
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

    @Subscribe(sticky = true)
    fun onDiaryEvaluationFilledEvent(diaryEvaluationFilledEvent: DiaryEvaluationFilledEvent) {
        queryData(true)
    }
}