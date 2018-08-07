package com.sumian.sleepdoctor.notification;

import com.sumian.sleepdoctor.base.SdBasePresenter;
import com.sumian.sleepdoctor.base.SdBaseView;
import com.sumian.sleepdoctor.notification.bean.Notification;

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

    interface Presenter extends SdBasePresenter {
        void loadMore();

        void readNotification(String notificationId);
    }

    interface View extends SdBaseView<Presenter> {
        void onLoadMore(List<Notification> notificationList, boolean hasMore);

        void onReadSuccess();
    }

}
