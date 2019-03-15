package com.sumian.sddoctor.notification

import android.content.Intent
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BaseActivity
import com.sumian.common.network.response.ErrorResponse
import com.sumian.module_core.async.AsyncCallback
import com.sumian.module_core.notification.Notification
import com.sumian.module_core.notification.NotificationListFragment
import com.sumian.module_core.notification.NotificationListResponse
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.buz.patientdoctorim.ConversationListActivity
import com.sumian.sddoctor.constants.StatConstants
import com.sumian.sddoctor.event.NotificationUnreadCountChangeEvent
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.util.EventBusUtil
import kotlinx.android.synthetic.main.activity_notification_list.*

class NotificationListActivity : BaseActivity(), NotificationListFragment.Host {

    private lateinit var mNotificationListFragment: NotificationListFragment
    override fun getLayoutId(): Int {
        return R.layout.activity_notification_list
    }

    override fun getPageName(): String {
        return StatConstants.page_message
    }

    override fun initWidget() {
        super.initWidget()
        title_bar.setOnBackClickListener { finish() }
        initFragment()
    }

    private fun initFragment() {
        val notificationListFragment = NotificationListFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, notificationListFragment).commit()
        mNotificationListFragment = notificationListFragment
    }

    override fun getNotificationList(page: Int, perPage: Int, callback: AsyncCallback<NotificationListResponse>) {
        val call = AppManager.getHttpService().getNotificationList(page, perPage, "all")
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<NotificationListResponse>() {
            override fun onSuccess(response: NotificationListResponse?) {
                callback.onSuccess(response)
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                callback.onFailed(errorResponse.code, errorResponse.message)
            }

            override fun onFinish() {
                callback.onFinish()
            }
        })
    }

    override fun readNotificationList(notificationId: String, dataId: Int, callback: AsyncCallback<Any>) {
        AppManager.getHttpService().readNotification(notificationId, dataId)
                .enqueue(object : BaseSdResponseCallback<Any>() {
                    override fun onSuccess(response: Any?) {
                        EventBusUtil.postStickyEvent(NotificationUnreadCountChangeEvent())
                        callback.onSuccess(response)
                    }

                    override fun onFailure(errorResponse: ErrorResponse) {
                        callback.onFailed(errorResponse.code, errorResponse.message)
                    }
                })
    }

    override fun onNotificationClick(notification: com.sumian.module_core.notification.Notification) {
        launchActivityIfNeed(notification)
    }

    override fun showReadAll(show: Boolean) {
        title_bar!!.setMenuVisibility(if (show) View.VISIBLE else View.GONE)
    }

    override fun launchPatientDoctorMessageListActivity() {
        ConversationListActivity.launch()
    }

    override fun isDoctor(): Boolean {
        return true
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        mNotificationListFragment.refreshData()
    }

    private fun launchActivityIfNeed(notification: Notification) {
        val intent = SchemeResolver.schemeResolver(this, notification.data.scheme)
        if (intent != null) {
            ActivityUtils.startActivity(intent)
        }
    }
}
