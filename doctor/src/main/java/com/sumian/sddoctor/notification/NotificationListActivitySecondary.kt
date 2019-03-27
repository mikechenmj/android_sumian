package com.sumian.sddoctor.notification

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.widget.adapter.BaseAdapter
import com.sumian.common.widget.adapter.BaseViewHolder
import com.sumian.module_core.notification.NotificationListResponse
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.base.SddBaseActivity
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import kotlinx.android.synthetic.main.list_item_notification_secondary.view.*
import kotlinx.android.synthetic.main.recycler_view_padding_top_10.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/27 10:38
 * desc   :
 * version: 1.0
 */
class NotificationListActivitySecondary : SddBaseActivity() {
    private val mAdapter by lazy { ListAdapter(recycler_view) }
    private val mPage = 1
    private val mType by lazy { intent.getIntExtra(KEY_TYPE, 0) }

    override fun getLayoutId(): Int {
        return R.layout.recycler_view_padding_top_10
    }

    override fun initWidget() {
        super.initWidget()
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = mAdapter
        mAdapter.setLoadMoreListener(object : BaseAdapter.LoadMoreListener {
            override fun loadMore() {
                loadData()
            }
        })
        mAdapter.setOnItemClickListener(object : BaseAdapter.OnItemClickListener<String> {
            override fun onItemClick(position: Int, viewHolder: BaseViewHolder, data: String) {
            }
        })
    }

    override fun initData() {
        super.initData()
        loadData()
    }

    private fun loadData() {
        val call = AppManager.getHttpService().getNotificationList(mPage, 20, "")
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<NotificationListResponse>() {
            override fun onSuccess(response: NotificationListResponse?) {

            }

            override fun onFailure(errorResponse: ErrorResponse) {
            }

            override fun onFinish() {
                super.onFinish()
                mAdapter.showLoadMore(false)
            }
        })
    }

    class ListAdapter(recyclerView: RecyclerView) : BaseAdapter<String>(recyclerView, R.layout.list_item_notification_secondary) {
        override fun onBindDataViewHolder(holder: BaseViewHolder, data: String) {
            holder.itemView.tv_title.text = data
            holder.itemView.tv_content.text = data
            holder.itemView.tv_title.text = data
        }
    }

    companion object {
        private const val KEY_TYPE = "key_type"

        fun launch(type: Int) {
            val bundle = Bundle()
            bundle.putInt(KEY_TYPE, type)
            ActivityUtils.startActivity(bundle, NotificationListActivitySecondary::class.java)
        }
    }
}