package com.sumian.module_core.notification

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.sumian.module_core.R
import kotlinx.android.synthetic.main.view_notification_list_head_view.view.*

/**
 * <pre>
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/6/5 14:56
 * desc   :
 * version: 1.0
</pre> *
 */
class NotificationListHeadView(context: Context) : FrameLayout(context) {
    init {
        inflate(context, R.layout.view_notification_list_head_view, this)
    }

    fun showNotificationItem(show: Boolean) {
        v_notification_item.visibility = if (show) View.VISIBLE else View.GONE
    }

    /**
     * msg: msg or null if no msg
     */
    fun showMessage(msg: String) {
        tv_doctor_message.text = msg
        iv_doctor_message_dot.visibility = View.GONE
    }

    fun showNoMessage() {
        tv_doctor_message.text = context.getString(R.string.no_doctor_message_yet)
        iv_doctor_message_dot.visibility = View.VISIBLE
    }

    fun showEmptyView(show: Boolean) {
        tv_no_data.visibility = if (show) View.VISIBLE else View.GONE
    }
}
