package com.sumian.sd.buz.notification;

import com.sumian.sd.base.SdBasePresenter;
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

    interface Presenter extends SdBasePresenter {
        void loadData(boolean isInitLoad);

        void readNotification(String notificationId, int notificationDataId);
    }

    interface View extends SdBaseView<Presenter> {
        void onLoadMore(List<Notification> notificationList, boolean hasMore);

        void onReadSuccess();
    }

}
