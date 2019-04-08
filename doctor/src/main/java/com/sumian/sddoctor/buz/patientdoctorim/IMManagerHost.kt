package com.sumian.sddoctor.buz.patientdoctorim

import cn.leancloud.chatkit.LCIMManager
import com.avos.avoscloud.im.v2.AVIMConversation
import com.avos.avoscloud.im.v2.AVIMTypedMessage
import com.sumian.common.notification.AppNotificationManager
import com.sumian.sddoctor.app.App

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/18 14:12
 * desc   :
 * version: 1.0
 */
class IMManagerHost : LCIMManager.Host {
    override fun onNewMessage(conversation: AVIMConversation?, message: AVIMTypedMessage?) {
        val context = App.getAppContext()
        val intent = LCIMManager.getInstance().getConversationIntent(context, conversation)
        AppNotificationManager.showNotification(context, "您收到了一条新的患者消息", intent)
    }
}