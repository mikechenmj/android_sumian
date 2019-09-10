@file:Suppress("UNUSED_ANONYMOUS_PARAMETER")

package com.sumian.sd.buz.scale

import androidx.recyclerview.widget.LinearLayoutManager
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
import com.sumian.sd.buz.scale.bean.FilledScaleCollection
import com.sumian.sd.buz.scale.event.ScaleFinishFillingEvent2
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import com.sumian.sd.common.utils.EventBusUtil
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
                val filledScaleCollection = mAdapter.getItem(position) as FilledScaleCollection
                ScaleDetailActivity.launch(activity!!, filledScaleCollection.title, filledScaleCollection.distributions.id, filledScaleCollection.id)
            }
        }
        val emptyErrorView = EmptyErrorView.create(activity!!, R.mipmap.ic_empty_state_report, R.string.empty_evaluation_msg, R.string.know_your_sleep_health_situation)
        emptyErrorView.mAutoHide = false
        emptyErrorView.setOnClickListener { loadMoreData() }
        mAdapter.emptyView = emptyErrorView
        recycler_view.layoutManager = LinearLayoutManager(activity!!)
        recycler_view.adapter = mAdapter
        loadMoreData()
    }

    private fun loadMoreData() {
        val call = AppManager.getSdHttpService().getFilledScaleCollections(mPage)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<PaginationResponseV2<FilledScaleCollection>>() {

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }

            override fun onSuccess(response: PaginationResponseV2<FilledScaleCollection>?) {
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

class FilledScaleListAdapter : BaseQuickAdapter<FilledScaleCollection, BaseViewHolder>(R.layout.item_scale_filled) {
    override fun convert(helper: BaseViewHolder, item: FilledScaleCollection) {
        helper.setText(R.id.tv_scale_name, item.title)
        helper.setText(R.id.tv_time, TimeUtilV2.formatYYYYMMDDHHMM(item.distributions.getUpdateAtInMillis()))
    }
}