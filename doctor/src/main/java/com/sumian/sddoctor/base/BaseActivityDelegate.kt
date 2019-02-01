package com.sumian.sddoctor.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.sumian.common.base.IActivityDelegate
import com.sumian.common.notification.AppNotificationManager

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/11/8 15:38
 * desc   : 由于 common 包下的 BaseActivity 在 sdd 中有很多实现，该类负责代理这些实现类共有逻辑。
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
        AppNotificationManager.markNotificationAsRead(intent)
    }

}