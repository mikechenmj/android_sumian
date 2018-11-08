package com.sumian.sd.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.sumian.common.base.IActivityDelegate
import com.sumian.sd.notification.bean.AppNotificationManager
import com.sumian.sd.utils.NotificationUtil

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/11/8 18:00
 * desc   :
 * version: 1.0
 */
class BaseActivityDelegate(activity: Activity) : IActivityDelegate {
    private var mActivity: Activity = activity

    override fun onCreate(savedInstanceState: Bundle?) {
        markNotificationAsReadIfNeed(getIntent())
    }

    override fun onNewIntent(intent: Intent?) {
        markNotificationAsReadIfNeed(intent)
    }

    override fun onStart() {
    }

    override fun onResume() {
    }

    override fun onPause() {
    }

    override fun onStop() {
    }

    override fun onDestroy() {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    }

    private fun getIntent(): Intent? {
        return mActivity.intent
    }

    private fun markNotificationAsReadIfNeed(intent: Intent?) {
        val notificationId = intent?.getStringExtra(NotificationUtil.KEY_PUSH_NOTIFICATION_ID)
        AppNotificationManager.markNotificationAsRead(notificationId)
    }
}