package com.sumian.common.notification

import android.content.Context
import com.avos.avoscloud.AVOSCloud
import com.avos.avoscloud.PushService
import com.avos.avoscloud.im.v2.AVIMClient

/**
 * <pre>
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/6/7 16:08
 * desc   :
 * version: 1.0
</pre> *
 */
object LeanCloudManager {

    fun init(context: Context, appId: String, appKey: String, pushChannel:String, isDebug: Boolean) {
        PushService.setDefaultChannelId(context, "push_channel")
        AVOSCloud.initialize(context, appId, appKey)
        AVOSCloud.setDebugLogEnabled(isDebug)
        AVIMClient.setAutoOpen(false)
        PushService.setAutoWakeUp(true)
        PushService.subscribe(context, pushChannel,null)
    }
}
