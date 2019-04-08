package com.sumian.sddoctor.notification

import android.content.Context
import android.content.Intent
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.network.error.ErrorCode
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.widget.CommonDividerItemDecoration
import com.sumian.common.widget.adapter.BaseAdapter
import com.sumian.common.widget.adapter.BaseViewHolder
import com.sumian.module_core.notification.Notification
import com.sumian.module_core.notification.NotificationListResponse
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.base.SddBaseActivity
import com.sumian.sddoctor.event.NotificationUnreadCountChangeEvent
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.util.EventBusUtil
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
    private var mPage = 1
    private val mCategory by lazy { intent.getIntExtra(KEY_CATEGORY, 0) }

    override fun getLayoutId(): Int {
        return R.layout.recycler_view_padding_top_10
    }

    override fun showBackNav(): Boolean {
        return true
    }

    override fun initWidget() {
        super.initWidget()
        mTitleBar.setMenuText(getString(R.string.read_all))
        mTitleBar.setOnMenuClickListener { readAll() }
        val title = if (mCategory == 2) R.string.patient_weekly_report else R.string.system_notification
        setTitle(title)
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = mAdapter
        recycler_view.addItemDecoration(CommonDividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        mAdapter.setLoadMoreListener(object : BaseAdapter.LoadMoreListener {
            override fun loadMore() {
                loadData()
            }
        })
        mAdapter.setOnItemClickListener(object : BaseAdapter.OnItemClickListener<Notification> {
            override fun onItemClick(position: Int, viewHolder: BaseViewHolder, data: Notification) {
                readNotification(data.id, data.dataId, position)
                val intent = SchemeResolver.schemeResolver(this@NotificationListActivitySecondary, data.data.scheme)
                ActivityUtils.startActivity(intent ?: return)
            }
        })
    }

    override fun initData() {
        super.initData()
        loadData()
    }

    private fun loadData() {
        val call = AppManager.getHttpService().getNotificationListByCategory(mPage, 20, "", mCategory)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<NotificationListResponse>() {
            override fun onSuccess(response: NotificationListResponse?) {
                if (response == null) return
                mAdapter.addData(response.data)
                mPage++
                mAdapter.enableLoadMore(!response.meta.pagination.isLastPage())
            }

            override fun onFailure(errorResponse: ErrorResponse) {
            }

            override fun onFinish() {
                super.onFinish()
                mAdapter.showLoadMore(false)
            }
        })
    }

    fun readNotification(notificationId: String, dataId: Int, position: Int) {
        AppManager.getHttpService().readNotification(notificationId, dataId)
                .enqueue(object : BaseSdResponseCallback<Any>() {
                    override fun onSuccess(response: Any?) {
                        EventBusUtil.postStickyEvent(NotificationUnreadCountChangeEvent())
                        onReadSuccess(notificationId, position)
                    }

                    override fun onFailure(errorResponse: ErrorResponse) {
                        if (errorResponse.code == ErrorCode.STATUS_CODE_ERROR_UNKNOWN) {
                            onReadSuccess(notificationId, position) // 成功的response body 为空response code = 202, 医生版是204就不会，会走到onFail分支
                        } else {
                            ToastUtils.showShort(errorResponse.message)
                        }
                    }
                })
    }

    fun onReadSuccess(notificationId: String, position: Int) {
        val list = mAdapter.getData()
        if (notificationId == "0") {
            val currentTimeMillis = System.currentTimeMillis()
            for (notification in list) {
                notification.readAt = (currentTimeMillis / 1000L).toInt()
            }
            mAdapter.notifyDataSetChanged()
        } else {
            val notification = list[position]
            notification.readAt = (System.currentTimeMillis() / 1000L).toInt()
            mAdapter.notifyDataItemChange(position)
        }
    }

    fun readAll() {
        readNotification("0", 0, 0)
    }

    class ListAdapter(recyclerView: RecyclerView) : BaseAdapter<Notification>(recyclerView, R.layout.list_item_notification_secondary) {
        override fun onBindDataViewHolder(holder: BaseViewHolder, data: Notification) {
            holder.itemView.tv_title.text = data.data.title
            holder.itemView.tv_content.text = data.data.content
            holder.itemView.tv_time.text = data.getCreatedAtString()
            holder.itemView.iv_dot.isVisible = data.readAt == 0
        }
    }

    companion object {
        private const val KEY_CATEGORY = "KEY_CATEGORY"

        fun launch(type: Int) {
            val intent = Intent(ActivityUtils.getTopActivity(), NotificationListActivitySecondary::class.java)
            intent.putExtra(KEY_CATEGORY, type)
            ActivityUtils.startActivity(intent)
        }

        fun launch(context: Context, type: Int) {
            val intent = Intent(ActivityUtils.getTopActivity(), NotificationListActivitySecondary::class.java)
            intent.putExtra(KEY_CATEGORY, type)
            context.startActivity(intent)
        }
    }
}