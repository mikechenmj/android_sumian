@file:Suppress("UNUSED_ANONYMOUS_PARAMETER")

package com.sumian.sd.buz.scale

import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.network.response.PaginationResponseV2
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.scale.bean.ReleasedScaleCollection
import com.sumian.sd.buz.scale.event.ScaleFinishFillingEvent
import com.sumian.sd.common.h5.H5Uri
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import com.sumian.sd.common.utils.EventBusUtil
import org.greenrobot.eventbus.Subscribe


/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/19 10:26
 * desc   :
 * version: 1.0
 */
class NotFilledScaleListFragment(
        override var mAdapter
        : BaseQuickAdapter<ReleasedScaleCollection, BaseViewHolder>
        = NotFilledScaleListAdapter())
    : ScaleListFragment<ReleasedScaleCollection>() {

    init {
        mAdapter.setOnItemClickListener { adapter, view, position ->
            val releasedScaleCollection = mAdapter.getItem(position) as ReleasedScaleCollection
            ScaleDetailActivity.launch(activity!!, releasedScaleCollection.title,
                    H5Uri.RELEASED_SCALE_COLLECTIONS
                            .replace("{collection_id}", releasedScaleCollection.id.toString()))
        }
    }

    override fun loadMoreData() {
        val call = AppManager.getSdHttpService().getReleasedScaleCollections(mPage)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<PaginationResponseV2<ReleasedScaleCollection>>() {

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }

            override fun onSuccess(response: PaginationResponseV2<ReleasedScaleCollection>?) {
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

    @Subscribe(sticky = true)
    fun onScaleFinishFillingEvent(event: ScaleFinishFillingEvent) {
        EventBusUtil.removeStickyEvent(event)
        refreshData()
    }
}

class NotFilledScaleListAdapter : BaseQuickAdapter<ReleasedScaleCollection, BaseViewHolder>(R.layout.item_scale_not_filled) {
    override fun convert(helper: BaseViewHolder, item: ReleasedScaleCollection) {
        helper.setText(R.id.tv_scale_name, item.title)
    }
}