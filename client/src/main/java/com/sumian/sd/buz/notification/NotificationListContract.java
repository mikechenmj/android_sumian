package com.sumian.sd.buz.notification;

import com.sumian.sd.base.SdBaseView;
import com.sumian.sd.buz.notification.bean.Notification;

import java.util.List;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/6 16:14
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class NotificationListContract {


    interface View extends SdBaseView<NotificationListPresenter> {
        void onLoadMore(List<Notification> notificationList, boolean hasMore);

        void onReadSuccess();
    }

}
