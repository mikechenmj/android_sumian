package com.sumian.sleepdoctor.push

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.blankj.utilcode.util.LogUtils

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/8 10:31
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class PushReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        LogUtils.d(intent?.action)
        val pushData = PushDataResolveUtil.getPushData(intent)
        LogUtils.d(pushData)
    }
}