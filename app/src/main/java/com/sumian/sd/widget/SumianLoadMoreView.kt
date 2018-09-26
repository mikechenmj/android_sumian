package com.sumian.sd.widget

import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.loadmore.LoadMoreView
import com.sumian.sd.R

class SumianLoadMoreView : LoadMoreView() {
    override fun getLayoutId(): Int {
        return R.layout.layout_sumian_load_more_view
    }

    override fun getLoadingViewId(): Int {
        return R.id.sm_load_more_loading_view
    }

    override fun getLoadFailViewId(): Int {
        return R.id.sm_load_more_load_fail_view
    }

    override fun getLoadEndViewId(): Int {
        return R.id.sm_load_more_load_end_view
    }

    override fun convert(holder: BaseViewHolder) {
        super.convert(holder)
        holder.setGone(R.id.root_view, loadMoreStatus == STATUS_END)
    }
}