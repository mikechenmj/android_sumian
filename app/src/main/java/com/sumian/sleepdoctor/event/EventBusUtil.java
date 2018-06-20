package com.sumian.sleepdoctor.event;

import org.greenrobot.eventbus.EventBus;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/7 14:46
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class EventBusUtil {
    public static void post(Object event) {
        EventBus.getDefault().post(event);
    }

    public static void postSticky(Object event) {
        EventBus.getDefault().postSticky(event);
    }

    public static void removeStickyEvent(Object event) {
        EventBus.getDefault().removeStickyEvent(event);
    }
}
