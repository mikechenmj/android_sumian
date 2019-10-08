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
import com.sumian.sd.common.h5.H5Uri
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
abstract class ScaleListFragment<T : Any> : BaseFragment() {
    override fun getLayoutId(): Int {
        return R.layout.recycler_view_with_top_padding
    }

    protected abstract var mAdapter: BaseQuickAdapter<T, BaseViewHolder>
    protected var mPage = 1

    override fun initWidget() {
        super.initWidget()
        mAdapter.setOnLoadMoreListener({ loadMoreData() }, recycler_view)
        mAdapter.emptyView = EmptyErrorView.create(activity!!, R.mipmap.ic_empty_state_report, R.string.empty_evaluation_msg, R.string.know_your_sleep_health_situation)
                .apply {
                    setAutoHide(false)
                    setOnEmptyCallback { refreshData() }
                }
        recycler_view.layoutManager = LinearLayoutManager(activity!!)
        recycler_view.adapter = mAdapter
        loadMoreData()
    }

    protected abstract fun loadMoreData()

    override fun onStart() {
        super.onStart()
        EventBusUtil.register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBusUtil.unregister(this)
    }

    protected fun refreshData() {
        mPage = 1
        loadMoreData()
    }
}