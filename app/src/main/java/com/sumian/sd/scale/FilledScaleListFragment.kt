@file:Suppress("UNUSED_ANONYMOUS_PARAMETER")

package com.sumian.sd.scale

import android.support.v7.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sumian.common.base.BaseFragment
import com.sumian.common.h5.widget.EmptyErrorView
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.network.response.PaginationResponseV2
import com.sumian.common.utils.TimeUtilV2
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.event.EventBusUtil
import com.sumian.sd.event.ScaleFinishFillingEvent2
import com.sumian.sd.network.callback.BaseSdResponseCallback
import com.sumian.sd.scale.bean.FilledScale
import kotlinx.android.synthetic.main.recycler_view_with_top_padding.*
import org.greenrobot.eventbus.Subscribe


/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/19 10:26
 * desc   :
 * version: 1.0
 */
class FilledScaleListFragment : BaseFragment() {
    override fun getLayoutId(): Int {
        return R.layout.recycler_view_with_top_padding
    }

    private var mAdapter = FilledScaleListAdapter()
    private var mPage = 1

    override fun initWidget() {
        super.initWidget()
        mAdapter.setOnLoadMoreListener({ loadMoreData() }, recycler_view)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            run {
                val filledScale = mAdapter.getItem(position) as FilledScale
                ScaleDetailActivity.launch(activity!!, filledScale.title, filledScale.latest_scale_distribution.id, filledScale.id)
            }
        }
        mAdapter.emptyView = EmptyErrorView.create(activity!!, R.mipmap.ic_empty_state_report, R.string.empty_evaluation_msg, R.string.know_your_sleep_health_situation)
        recycler_view.layoutManager = LinearLayoutManager(activity!!)
        recycler_view.adapter = mAdapter
        loadMoreData()
    }

    private fun loadMoreData() {
        val call = AppManager.getSdHttpService().getFilledScaleList(mPage)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<PaginationResponseV2<FilledScale>>() {

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }

            override fun onSuccess(response: PaginationResponseV2<FilledScale>?) {
                val data = response!!.data
                if (mPage == 1) {
                    mAdapter.setNewData(data)
                } else {
                    mAdapter.addData(data)
                }
                mAdapter.setEnableLoadMore(!response.meta.pagination.isLastPage())
                mPage++
            }

            override fun onFinish() {
                super.onFinish()
                mAdapter.loadMoreComplete()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        EventBusUtil.register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBusUtil.unregister(this)
    }

    @Subscribe(sticky = true)
    fun onScaleFinishFillingEvent(event: ScaleFinishFillingEvent2) {
        EventBusUtil.removeStickyEvent(event)
        refreshData()
    }

    private fun refreshData() {
        mPage = 1
        loadMoreData()
    }

}

class FilledScaleListAdapter : BaseQuickAdapter<FilledScale, BaseViewHolder>(R.layout.item_scale_filled) {
    override fun convert(helper: BaseViewHolder, item: FilledScale) {
        helper.setText(R.id.tv_scale_name, item.title)
        helper.setText(R.id.tv_time, TimeUtilV2.formatTimeYYYYMMDD_HHMM(item.latest_scale_distribution.getUpdateAtInMillis()))
    }
}