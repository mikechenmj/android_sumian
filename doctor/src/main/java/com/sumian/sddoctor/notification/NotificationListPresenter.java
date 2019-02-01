package com.sumian.sddoctor.notification;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.sumian.common.network.response.ErrorResponse;
import com.sumian.sddoctor.app.AppManager;
import com.sumian.sddoctor.event.NotificationUnreadCountChangeEvent;
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback;
import com.sumian.sddoctor.notification.bean.GetNotificationListResponse;
import com.sumian.sddoctor.notification.bean.Notification;
import com.sumian.sddoctor.util.EventBusUtil;

import java.util.List;

import androidx.annotation.NonNull;
import retrofit2.Call;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/6 16:27
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class NotificationListPresenter implements NotificationListContract.Presenter {
    private static final int PER_PAGE = 10;
    private NotificationListContract.View mView;
    private int mPage = 1;

    NotificationListPresenter(NotificationListContract.View view) {
        mView = view;
    }

    @Override
    public void loadMore() {
        mView.showLoading();
        Call<GetNotificationListResponse> call = AppManager.getHttpService().getNotificationList(mPage, PER_PAGE, "all");
        call.enqueue(new BaseSdResponseCallback<GetNotificationListResponse>() {
            @Override
            protected void onSuccess(GetNotificationListResponse response) {
                LogUtils.d(response);
                List<Notification> data = response.getData();
                mView.onLoadMore(data, data.size() == PER_PAGE);
                mPage++;
            }

            @Override
            protected void onFailure(@NonNull ErrorResponse errorResponse) {
                LogUtils.d(errorResponse);
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                mView.dismissLoading();
            }
        });
    }

    /**
     * @param notificationId notification id or "0" for reading all
     */
    @Override
    public void readNotification(@NonNull String notificationId, int notificationDataId, int position) {
        AppManager.getHttpService().readNotification(notificationId, notificationDataId)
                .enqueue(new BaseSdResponseCallback<Object>() {
                    @Override
                    protected void onSuccess(Object response) {
                        LogUtils.d(response);
                        mView.onReadSuccess(notificationId, position);
                        EventBusUtil.Companion.postStickyEvent(new NotificationUnreadCountChangeEvent());
                    }

                    @Override
                    protected void onFailure(@NonNull ErrorResponse errorResponse) {
                        LogUtils.d(errorResponse);
                        ToastUtils.showLong(errorResponse.getMessage());
                    }
                });
    }
}
