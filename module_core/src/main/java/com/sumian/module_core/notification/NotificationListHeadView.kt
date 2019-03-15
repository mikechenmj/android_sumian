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
class NotificationListHeadView(context: Context, isDoctor: Boolean) : FrameLayout(context) {
    val mIsDoctor = isDoctor

    init {
        inflate(context, R.layout.view_notification_list_head_view, this)
        tv_im_item_title.text = resources.getString(if (mIsDoctor) R.string.patient_message else R.string.doctor_message)
    }

    fun showNotificationItem(show: Boolean) {
        v_notification_item.visibility = if (show) View.VISIBLE else View.GONE
    }

    /**
     * msg: msg or null if no msg
     */
    fun showMessage(msg: String) {
        tv_im_item_message.text = msg
        iv_doctor_message_dot.visibility = View.VISIBLE
    }

    fun showNoMessage() {
        tv_im_item_message.text = context.getString(if (mIsDoctor) R.string.no_patient_message_yet else R.string.no_doctor_message_yet)
        iv_doctor_message_dot.visibility = View.GONE
    }

    fun showNoNotificationView(show: Boolean) {
        tv_no_data.visibility = if (show) View.VISIBLE else View.GONE
    }

}
