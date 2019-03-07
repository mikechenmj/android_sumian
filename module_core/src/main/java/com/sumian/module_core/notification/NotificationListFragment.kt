package com.sumian.module_core.notification

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.sumian.common.base.BaseFragment
import com.sumian.common.network.error.ErrorCode
import com.sumian.common.notification.NotificationUtil
import com.sumian.common.utils.SettingsUtil
import com.sumian.module_core.R
import com.sumian.module_core.async.AsyncCallback
import kotlinx.android.synthetic.main.fragment_notification_list.*
import kotlinx.android.synthetic.main.view_notification_list_head_view.view.*
import java.util.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/7 11:46
 * desc   :
 * version: 1.0
 */
class NotificationListFragment : BaseFragment(), BaseQuickAdapter.OnItemClickListener {

    lateinit var mHost: Host
    private var mAdapter: NotificationListAdapter? = null
    private var mHeaderView: NotificationListHeadView? = null
    private val PER_PAGE = 15
    private val INIT_PAGE = 1
    private var mPage = INIT_PAGE
    private val isNotificationEnabled: Boolean
        get() = NotificationUtil.areNotificationsEnabled(activity)

    override fun getLayoutId(): Int {
        return R.layout.fragment_notification_list
    }

    override fun initWidget() {
        super.initWidget()
        recycler_view.layoutManager = LinearLayoutManager(activity)
        mAdapter = NotificationListAdapter(activity, null)
        recycler_view.adapter = mAdapter
        mAdapter!!.onItemClickListener = this
        mAdapter!!.setOnLoadMoreListener({ loadData(false) }, recycler_view)
        initHeadView()
    }

    private fun initHeadView() {
        mHeaderView = NotificationListHeadView(activity!!)
        mHeaderView!!.setOnClickListener { v -> SettingsUtil.launchSettingActivityForResult(this, REQUEST_CODE_OPEN_NOTIFICATION) }
        mAdapter!!.addHeaderView(mHeaderView)
        updateNotificationItem()
        mHeaderView!!.v_doctor_message_item.setOnClickListener { mHost.launchPatientDoctorMessageListActivity() }
    }

    override fun initData() {
        super.initData()
        loadData(true)
    }

    fun refreshData() {
        mAdapter!!.setNewData(null)
        loadData(true)
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        val notification = mAdapter!!.getItem(position) ?: return
        markAsRead(notification, position)
        mHost.onNotificationClick(notification)
    }

    private fun markAsRead(notification: Notification, position: Int) {
        readNotification(notification.id, notification.dataId, position)
    }

    fun markAllAsRead() {
        readNotification("0", 0, 0)
    }

    fun onLoadMore(notificationList: List<Notification>?, hasMore: Boolean) {
        var notificationList = notificationList
        if (notificationList == null) {
            notificationList = ArrayList()
        }
        mAdapter!!.addData(notificationList)
        mAdapter!!.loadMoreComplete()
        mAdapter!!.setEnableLoadMore(hasMore)
        mHeaderView!!.showEmptyView(!hasMore && mAdapter!!.data.size == 0)
        mHost.showReadAll(mAdapter!!.itemCount > 0)
    }

    override fun onStart() {
        super.onStart()
        updateNotificationItem()
    }

    fun updateNotificationItem() {
        mHeaderView!!.showNotificationItem(!isNotificationEnabled)
    }

    companion object {

        val REQUEST_CODE_OPEN_NOTIFICATION = 1

    }

    interface Host {
        fun getNotificationList(page: Int, perPage: Int, callback: AsyncCallback<NotificationListResponse>)
        fun readNotificationList(notificationId: String, dataId: Int, callback: AsyncCallback<Any>)
        fun onNotificationClick(notification: Notification)
        fun showReadAll(show: Boolean)
        fun launchPatientDoctorMessageListActivity()
    }

    private fun loadData(isInitLoad: Boolean) {
        if (isInitLoad) {
            mPage = INIT_PAGE
        }
        mHost.getNotificationList(mPage, PER_PAGE, object : AsyncCallback<NotificationListResponse> {
            override fun onSuccess(result: NotificationListResponse?) {
                LogUtils.d(result)
                val data = result?.data
                onLoadMore(data, data != null && data.size == PER_PAGE)
                mPage++
            }

            override fun onFailed(code: Int, message: String?) {
                LogUtils.d(message)
                onLoadMore(null, code == ErrorCode.NOT_FOUND)
            }

            override fun onFinish() {
            }
        })
    }

    private fun readNotification(notificationId: String, notificationDataId: Int, position: Int) {
        mHost.readNotificationList(notificationId, notificationDataId, object : AsyncCallback<Any> {
            override fun onSuccess(result: Any?) {
                LogUtils.d(result)
                onReadSuccess(notificationId, position)
            }

            override fun onFailed(code: Int, message: String?) {
                LogUtils.d(message)
                ToastUtils.showLong(message)
            }

            override fun onFinish() {
            }
        })
    }

    fun onReadSuccess(notificationId: String, position: Int) {
        val list = mAdapter!!.data
        if (notificationId == "0") {
            val currentTimeMillis = System.currentTimeMillis()
            for (notification in list) {
                notification.readAt = (currentTimeMillis / 1000L).toInt()
            }
            mAdapter!!.notifyDataSetChanged()
        } else {
            val notification = list[position]
            notification.readAt = (System.currentTimeMillis() / 1000L).toInt()
            mAdapter!!.setData(position, notification)
        }
    }

}