package com.sumian.sd.buz.notification

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.sumian.common.base.BaseViewModelActivity
import com.sumian.common.notification.NotificationUtil
import com.sumian.common.utils.SettingsUtil
import com.sumian.sd.R
import com.sumian.sd.buz.doctormessage.DoctorMessageListActivity
import com.sumian.sd.buz.notification.bean.Notification
import com.sumian.sd.buz.stat.StatConstants
import com.sumian.sd.widget.TitleBar
import kotlinx.android.synthetic.main.view_notification_list_head_view.view.*
import java.util.*


class NotificationListActivity : BaseViewModelActivity<NotificationListPresenter>(), BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.RequestLoadMoreListener {
    private var titleBar: TitleBar? = null
    private var mAdapter: NotificationListAdapter? = null
    private var mHeaderView: NotificationListHeadView? = null
    private val isNotificationEnabled: Boolean
        get() = NotificationUtil.areNotificationsEnabled(this)

    override fun getLayoutId(): Int {
        return R.layout.activity_notification_list
    }

    override fun getPageName(): String {
        return StatConstants.page_notification_list
    }

    override fun initWidget() {
        super.initWidget()
        mViewModel = NotificationListPresenter(this)
        titleBar = findViewById(R.id.title_bar)
        titleBar!!.setOnBackClickListener { v -> finish() }
        titleBar!!.setOnMenuClickListener { v -> markAllAsRead() }
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = NotificationListAdapter(this, null)
        recyclerView.adapter = mAdapter
        mAdapter!!.onItemClickListener = this
        mAdapter!!.setOnLoadMoreListener(this, recyclerView)
//        mAdapter!!.emptyView = emptyView
        initHeadView()
    }

    private fun initHeadView() {
        mHeaderView = NotificationListHeadView(this)
        mHeaderView!!.setOnClickListener { v -> SettingsUtil.launchSettingActivityForResult(this, REQUEST_CODE_OPEN_NOTIFICATION) }
        mAdapter!!.addHeaderView(mHeaderView)
        updateNotificationItem()
        mHeaderView!!.v_doctor_message_item.setOnClickListener { DoctorMessageListActivity.launch() }
    }

    override fun initData() {
        super.initData()
        mViewModel!!.loadData(true)
    }

    /**
     * 重复启动（点击notification）会进入该分支，需要刷新数据
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        mAdapter!!.setNewData(null)
        mViewModel!!.loadData(true)
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        val notification = mAdapter!!.getItem(position) ?: return
        markAsRead(notification, position)
        launchActivityIfNeed(notification)
    }

    private fun markAsRead(notification: Notification, position: Int) {
        mViewModel!!.readNotification(notification.id, notification.dataId)
        notification.readAt = (System.currentTimeMillis() / 1000L).toInt()
        mAdapter!!.setData(position, notification)
    }

    private fun markAllAsRead() {
        mViewModel!!.readNotification("0", 0)
        val data = mAdapter!!.data
        val currentTimeMillis = System.currentTimeMillis()
        for (notification in data) {
            notification.readAt = (currentTimeMillis / 1000L).toInt()
        }
        mAdapter!!.notifyDataSetChanged()
    }

    private fun launchActivityIfNeed(notification: Notification) {
        val type = notification.type
        if (Notification.TYPE_FOLLOW_UP_LIFE_NOTICE == type || Notification.TYPE_FOLLOW_UP_REFERRAL_NOTICE == type) {
            return
        }
        val scheme = notification.data.scheme
        val intent = SchemeResolver.schemeResolver(this, scheme)
        if (intent == null) {
            LogUtils.d("Unresolved scheme", scheme)
            return
        }
        startActivity(intent)
    }

    fun onLoadMore(notificationList: List<Notification>?, hasMore: Boolean) {
        var notificationList = notificationList
        titleBar!!.setMenuVisibility(if (mAdapter!!.itemCount > 0) View.VISIBLE else View.GONE)
        if (notificationList == null) {
            notificationList = ArrayList()
        }
        mAdapter!!.addData(notificationList)
        mAdapter!!.loadMoreComplete()
        mAdapter!!.setEnableLoadMore(hasMore)
        mHeaderView!!.showEmptyView(!hasMore && mAdapter!!.data.size == 0)
    }

    fun onReadSuccess() {}

    override fun onLoadMoreRequested() {
        mViewModel!!.loadData(false)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_OPEN_NOTIFICATION) {
            updateNotificationItem()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun onBegin() {

    }

    fun onFinish() {

    }

    private fun updateNotificationItem() {
        mHeaderView!!.showNotificationItem(!isNotificationEnabled)
    }

    companion object {

        val REQUEST_CODE_OPEN_NOTIFICATION = 1

        fun launch(context: Context) {
            ActivityUtils.startActivity(getLaunchIntent(context))
        }

        fun getLaunchIntent(context: Context): Intent {
            return Intent(context, NotificationListActivity::class.java)
        }
    }
}
