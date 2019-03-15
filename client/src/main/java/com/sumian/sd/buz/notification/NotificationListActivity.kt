package com.sumian.sd.buz.notification

import android.content.Context
import android.content.Intent
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.sumian.common.base.BaseViewModel
import com.sumian.common.base.BaseViewModelActivity
import com.sumian.common.network.error.ErrorCode
import com.sumian.common.network.response.ErrorResponse
import com.sumian.module_core.async.AsyncCallback
import com.sumian.module_core.notification.Notification
import com.sumian.module_core.notification.NotificationListFragment
import com.sumian.module_core.notification.NotificationListResponse
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.patientdoctorim.ConversationListActivity
import com.sumian.sd.buz.stat.StatConstants
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import com.sumian.sd.common.utils.EventBusUtil
import kotlinx.android.synthetic.main.activity_notification_list.*

class NotificationListActivity : BaseViewModelActivity<BaseViewModel>(), NotificationListFragment.Host {

    private lateinit var mNotificationListFragment: NotificationListFragment

    override fun getLayoutId(): Int {
        return R.layout.activity_notification_list
    }

    override fun getPageName(): String {
        return StatConstants.page_notification_list
    }

    override fun initWidget() {
        super.initWidget()
        title_bar!!.setOnBackClickListener { v -> finish() }
        initFragment()
    }

    private fun initFragment() {
        val notificationListFragment = NotificationListFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, notificationListFragment).commit()
        mNotificationListFragment = notificationListFragment
    }

    override fun getNotificationList(page: Int, perPage: Int, callback: AsyncCallback<NotificationListResponse>) {
        val call = AppManager.getSdHttpService().getNotificationList(page, perPage)
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
        AppManager.getSdHttpService().readNotification(notificationId, dataId)
                .enqueue(object : BaseSdResponseCallback<Any>() {
                    override fun onSuccess(response: Any?) {
                        EventBusUtil.postStickyEvent(NotificationUnreadCountChangeEvent())
                        callback.onSuccess(response)
                    }

                    override fun onFailure(errorResponse: ErrorResponse) {
                        if (errorResponse.code == ErrorCode.STATUS_CODE_ERROR_UNKNOWN) {
                            callback.onSuccess(null) // 成功的response body 为空response code = 202, 医生版是204就不会，会走到onFail分支-_-||
                        } else {
                            callback.onFailed(errorResponse.code, errorResponse.message)
                        }
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
        return false
    }

    /**
     * 重复启动（点击notification）会进入该分支，需要刷新数据
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        mNotificationListFragment.refreshData()
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

    companion object {

        fun launch(context: Context) {
            ActivityUtils.startActivity(getLaunchIntent(context))
        }

        fun getLaunchIntent(context: Context): Intent {
            return Intent(context, NotificationListActivity::class.java)
        }
    }
}
