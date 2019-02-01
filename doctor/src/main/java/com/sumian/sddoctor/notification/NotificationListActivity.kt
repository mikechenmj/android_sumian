package com.sumian.sddoctor.notification

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ActivityUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.sumian.common.utils.SettingsUtil
import com.sumian.sddoctor.R
import com.sumian.sddoctor.base.BaseActivity
import com.sumian.sddoctor.homepage.HomepageFragment.Companion.REQUEST_CODE_OPEN_NOTIFICATION
import com.sumian.sddoctor.notification.bean.Notification
import com.sumian.sddoctor.util.NotificationUtil
import com.sumian.sddoctor.widget.EmptyErrorView
import kotlinx.android.synthetic.main.activity_notification_list.*

class NotificationListActivity : BaseActivity(), BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.RequestLoadMoreListener, NotificationListContract.View {

    private val mAdapter: NotificationListAdapter by lazy { NotificationListAdapter(this, null) }
    private var mHeaderView: NotificationListHeadView? = null
    private val mPresenter: NotificationListPresenter by lazy { NotificationListPresenter(this) }

    override fun getContentId(): Int {
        return R.layout.activity_notification_list
    }

    override fun initWidget() {
        super.initWidget()
        title_bar.setOnBackClickListener { finish() }
        title_bar.setOnMenuClickListener { markAllAsRead() }
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = mAdapter
        mAdapter.onItemClickListener = this
        mAdapter.setOnLoadMoreListener(this, recycler_view)
        initHeadView()
    }

    override fun initData() {
        super.initData()
        mPresenter.loadMore()
    }

    private fun initHeadView() {
        val enabled = isNotificationEnabled()
        if (!enabled) {
            mHeaderView = NotificationListHeadView(this)
            mHeaderView?.setOnClickListener { SettingsUtil.launchSettingActivityForResult(this, REQUEST_CODE_OPEN_NOTIFICATION) }
            mAdapter.addHeaderView(mHeaderView)
        }
    }

    private fun isNotificationEnabled(): Boolean {
        return NotificationUtil.areNotificationsEnabled(this)
    }

    private fun removeHeadViewInNeeded() {
        val enabled = isNotificationEnabled()
        if (enabled && mHeaderView != null) {
            mAdapter.removeHeaderView(mHeaderView)
            mHeaderView = null
        }
    }

    private fun getEmptyView(): View {
        return EmptyErrorView.create(this,
                R.mipmap.ic_empty_state_alarm,
                R.string.notification_list_empty_title,
                R.string.notification_list_empty_desc)
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        val notification = mAdapter.getItem(position) ?: return
        markAsRead(notification, position)
        launchActivityIfNeed(notification)
    }

    private fun markAsRead(notification: Notification, position: Int) {
        mPresenter.readNotification(notification.id, notification.dataId, position)
        notification.readAt = (System.currentTimeMillis() / 1000L).toInt()
        mAdapter.setData(position, notification)
    }

    private fun markAllAsRead() {
        mPresenter.readNotification("0", 0, 0)
    }

    override fun onReadSuccess(notificationId: String, position: Int) {
        val list = mAdapter.data
        if (notificationId == "0") {
            val currentTimeMillis = System.currentTimeMillis()
            for (notification in list) {
                notification.readAt = (currentTimeMillis / 1000L).toInt()
            }
            mAdapter.notifyDataSetChanged()
        } else {
            val notification = list[position]
            notification.readAt = (System.currentTimeMillis() / 1000L).toInt()
            mAdapter.setData(position, notification)
        }
    }

    private fun launchActivityIfNeed(notification: Notification) {
        val intent = SchemeResolver.schemeResolver(this, notification.data.scheme)
        if (intent != null) {
            ActivityUtils.startActivity(intent)
        }
    }

    override fun onLoadMore(notificationList: List<Notification>, hasMore: Boolean) {
        mAdapter.addData(notificationList)
        mAdapter.loadMoreComplete()
        mAdapter.setEnableLoadMore(hasMore)

        val hasData = mAdapter.data.size > 0
        title_bar.setMenuVisibility(if (hasData) View.VISIBLE else View.GONE)
        empty_error_view.visibility = if (hasData) View.GONE else View.VISIBLE
    }

    override fun onLoadMoreRequested() {
        mPresenter.loadMore()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_OPEN_NOTIFICATION) {
            removeHeadViewInNeeded()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
