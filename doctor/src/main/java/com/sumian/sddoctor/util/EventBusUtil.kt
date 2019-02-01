package com.sumian.sddoctor.util

import org.greenrobot.eventbus.EventBus

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/13 18:16
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class EventBusUtil {
    companion object {
        fun postEvent(event: Any) {
            EventBus.getDefault().post(event)
        }

        fun postStickyEvent(event: Any) {
            EventBus.getDefault().postSticky(event)
        }

        fun removeStickyEvent(event: Any) {
            EventBus.getDefault().removeStickyEvent(event)
        }

        fun register(subscriber: Any) {
            EventBus.getDefault().register(subscriber)
        }

        fun unregister(subscriber: Any) {
            EventBus.getDefault().unregister(subscriber)
        }
    }
}
