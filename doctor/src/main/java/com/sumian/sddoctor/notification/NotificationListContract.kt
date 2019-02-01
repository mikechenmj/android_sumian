package com.sumian.sddoctor.notification

import com.sumian.sddoctor.base.BasePresenter
import com.sumian.sddoctor.base.BaseView
import com.sumian.sddoctor.notification.bean.Notification

/**
 * <pre>
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/6/6 16:14
 * desc   :
 * version: 1.0
</pre> *
 */
internal class NotificationListContract {
    internal interface View : BaseView {
        fun onLoadMore(notificationList: List<Notification>, hasMore: Boolean)

        fun onReadSuccess(notificationId: String, position: Int)
    }

    internal interface Presenter : BasePresenter {
        fun loadMore()

        fun readNotification(notificationId: String, notificationDataId: Int, position: Int)
    }
}
