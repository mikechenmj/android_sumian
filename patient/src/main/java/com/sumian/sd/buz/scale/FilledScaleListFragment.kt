@file:Suppress("UNUSED_ANONYMOUS_PARAMETER")

package com.sumian.sd.buz.scale

import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.network.response.PaginationResponseV2
import com.sumian.common.utils.TimeUtilV2
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.scale.bean.FilledScaleCollection
import com.sumian.sd.buz.scale.event.ScaleFinishFillingEvent2
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
class FilledScaleListFragment(
        override var mAdapter
        : BaseQuickAdapter<FilledScaleCollection.CollectionDistributions, BaseViewHolder>
        = FilledScaleListAdapter())
    : ScaleListFragment<FilledScaleCollection.CollectionDistributions>() {

    init {
        mAdapter.setOnItemClickListener { adapter, view, position ->
            val distributions = mAdapter.getItem(position)
            ScaleDetailActivity.launch(activity!!, distributions!!.title,
                    H5Uri.FILLED_SCALE_COLLECTIONS
                            .replace("{collection_id}", distributions!!.collectionId.toString())
                            .replace("{distribution_id}", distributions!!.id.toString()))
        }
    }

    override fun loadMoreData() {
        val call = AppManager.getSdHttpService().getFilledScaleCollections(mPage)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<PaginationResponseV2<FilledScaleCollection>>() {

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }

            override fun onSuccess(response: PaginationResponseV2<FilledScaleCollection>?) {
                val data = mutableListOf<FilledScaleCollection.CollectionDistributions>()
                for (filledScaleCollection in response!!.data) {
                    for (collectionDistributions in filledScaleCollection.distributions) {
                        collectionDistributions.title = filledScaleCollection.title
                        collectionDistributions.collectionId = filledScaleCollection.id
                        data.add(collectionDistributions)
                    }
                }
                data.sortByDescending { it.updated_at }
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
    fun onScaleFinishFillingEvent(event: ScaleFinishFillingEvent2) {
        EventBusUtil.removeStickyEvent(event)
        refreshData()
    }
}

class FilledScaleListAdapter : BaseQuickAdapter<FilledScaleCollection.CollectionDistributions, BaseViewHolder>(R.layout.item_scale_filled) {
    override fun convert(helper: BaseViewHolder, item: FilledScaleCollection.CollectionDistributions) {
        helper.setText(R.id.tv_scale_name, item.title)
        helper.setText(R.id.tv_time, TimeUtilV2.formatYYYYMMDDHHMMss(item.getUpdateAtInMillis()))
    }
}