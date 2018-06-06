package com.sumian.sleepdoctor.notification;

import android.content.Context;
import android.widget.FrameLayout;

import com.sumian.sleepdoctor.R;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/5 14:56
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class NotificationListHeadView extends FrameLayout {
    public NotificationListHeadView(Context context) {
        super(context);
        inflate(context, R.layout.view_notification_list_head_view, this);
    }
}
