package com.sumian.module_core.notification

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import cn.leancloud.chatkit.LCIMManager
import cn.leancloud.chatkit.utils.LCIMConversationUtils
import cn.leancloud.chatkit.utils.LCIMLogUtils
import cn.leancloud.chatkit.utils.LCIMMessageUtil
import com.avos.avoscloud.AVCallback
import com.avos.avoscloud.AVException
import com.avos.avoscloud.im.v2.AVIMConversation
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
import kotlinx.android.synthetic.main.item_notification_group.view.*
import kotlinx.android.synthetic.main.view_notification_list_head_view.view.*

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Host) {
            mHost = context
        }
    }

    override fun initWidget() {
        super.initWidget()
        recycler_view.layoutManager = LinearLayoutManager(activity)
        mAdapter = NotificationListAdapter(activity, null)
        recycler_view.adapter = mAdapter
        mAdapter!!.onItemClickListener = this
        mAdapter!!.setOnLoadMoreListener({ loadData(false) }, recycler_view)
        initHeadView()
        LCIMManager.getInstance().unreadConversationsLiveData.observe(this, androidx.lifecycle.Observer<List<AVIMConversation>> { updateImItem() })
    }

    private fun initHeadView() {
        mHeaderView = NotificationListHeadView(activity!!, mHost.isDoctor())
        mHeaderView!!.v_notification_item.setOnClickListener { v -> SettingsUtil.launchSettingActivityForResult(this, REQUEST_CODE_OPEN_NOTIFICATION) }
        mAdapter!!.addHeaderView(mHeaderView)
        updateNotificationItem()
        mHeaderView!!.v_doctor_message_item.setOnClickListener { mHost.launchPatientDoctorMessageListActivity() }
        mHeaderView!!.tv_read_all.setOnClickListener { markAllAsRead() }
        mHeaderView!!.tv_read_all.isVisible = !mHost.isDoctor()
    }

    override fun onResume() {
        super.onResume()
        refreshData()
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
        mHeaderView!!.showNoNotificationView(!hasMore && mAdapter!!.data.size == 0)
        mHost.showReadAll(mAdapter!!.itemCount > 0)
    }

    override fun onStart() {
        super.onStart()
        updateNotificationItem()
        LCIMManager.getInstance().updateUnreadConversation()
    }

    fun updateNotificationItem() {
        mHeaderView!!.showNotificationItem(!isNotificationEnabled)
    }

    companion object {

        val REQUEST_CODE_OPEN_NOTIFICATION = 1

    }

    interface Host {
        fun getNotificationList(page: Int, perPage: Int, callback: AsyncCallback<NotificationListResponse>)
        fun readNotification(notificationId: String, dataId: Int, callback: AsyncCallback<Any>)
        fun onNotificationClick(notification: Notification)
        fun showReadAll(show: Boolean)
        fun launchPatientDoctorMessageListActivity()
        fun isDoctor(): Boolean
        fun getNotificationCategoryList(asyncCallback: AsyncCallback<List<NotificationCategory>>)
        fun launchNotificationListSecondaryActivity(category: Int)
    }

    private fun loadData(isInitLoad: Boolean) {
        if (mHost.isDoctor()) {
            updateDoctorNotificationList()
        } else {
            updateClientNotificationList(isInitLoad)
        }
    }

    private fun updateClientNotificationList(isInitLoad: Boolean) {
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
        mHost.readNotification(notificationId, notificationDataId, object : AsyncCallback<Any> {
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

    private fun updateImItem() {
        val unreadMessagesCount = LCIMManager.getInstance().unreadMessageCount
        mHeaderView?.showDot(unreadMessagesCount > 0)

        val latestConversation = LCIMManager.getInstance().latestConversation
        if (latestConversation != null) {
            LCIMConversationUtils.getConversationName(latestConversation, object : AVCallback<String>() {
                override fun internalDone0(name: String, e: AVException?) {
                    if (null != e) {
                        LCIMLogUtils.logException(e)
                    } else {
                        val message = LCIMMessageUtil.getMessageShorthand(mActivity, latestConversation.lastMessage)
                        mHeaderView?.showMessage("$name: $message")
                    }
                }
            })
        } else {
            mHeaderView?.showNoMessage()
        }
    }

    private fun updateDoctorNotificationList() {
        mHost.getNotificationCategoryList(object : AsyncCallback<List<NotificationCategory>> {
            override fun onSuccess(result: List<NotificationCategory>?) {
                val ncList = result ?: ArrayList<NotificationCategory>()
                mHeaderView?.showNoNotificationView(ncList.isEmpty())
                notification_list_fragment_item_container.removeAllViews()
                for (nc in ncList) {
                    val item = inflateItem(nc)
                    item.setOnClickListener { mHost.launchNotificationListSecondaryActivity(nc.category) }
                    notification_list_fragment_item_container.addView(item)
                }
            }

            override fun onFailed(code: Int, message: String?) {
            }

            override fun onFinish() {
            }
        })
    }

    private fun inflateItem(nc: NotificationCategory): View {
        val item = LayoutInflater.from(activity).inflate(R.layout.item_notification_group, notification_list_fragment_item_container, false)
        item.iv_left.setImageResource(if (nc.category == 2) R.drawable.news_icon_weekly else R.drawable.news_icon_notice)
        item.tv_label.text = getString(if (nc.category == 2) R.string.patient_weekly_report else R.string.system_notification)
        item.tv_content.text = nc.notification.data.content
        item.tv_time.text = nc.notification.getCreatedAtString()
        item.iv_dot.isVisible = nc.hasUnread()
        return item
    }

}