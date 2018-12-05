package com.sumian.sd.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.sumian.common.base.IActivityDelegate
import com.sumian.common.notification.AppNotificationManager
import com.sumian.sd.log.SdLogManager

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
        SdLogManager.logPage(mActivity.javaClass.simpleName, true)
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
        SdLogManager.logPage(mActivity.javaClass.simpleName, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    }

    private fun getIntent(): Intent? {
        return mActivity.intent
    }

    private fun markNotificationAsReadIfNeed(intent: Intent?) {
        AppNotificationManager.markNotificationAsRead(intent)
    }
}