package com.sumian.sleepdoctor.notification;

import android.support.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback;
import com.sumian.sleepdoctor.network.response.ErrorResponse;
import com.sumian.sleepdoctor.notification.bean.Notification;
import com.sumian.sleepdoctor.notification.bean.QueryNotificationResponse;

import java.util.List;

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
    private static final int PER_PAGE = 15;
    private NotificationListContract.View mView;
    private int mPage = 1;

    NotificationListPresenter(NotificationListContract.View view) {
        mView = view;
    }

    @Override
    public void loadMore() {
        mView.onBegin();
        Call<QueryNotificationResponse> call = AppManager.getHttpService().getNotificationList(mPage, PER_PAGE);
        addCall(call);
        call.enqueue(new BaseResponseCallback<QueryNotificationResponse>() {
            @Override
            protected void onSuccess(QueryNotificationResponse response) {
                LogUtils.d(response);
                List<Notification> data = response.getData();
                mView.onLoadMore(data, data.size() == PER_PAGE);
                mPage++;
            }

            @Override
            protected void onFailure(@NonNull ErrorResponse errorResponse) {
                LogUtils.d(errorResponse);
                mView.onLoadMore(null, errorResponse.isNotFound());
            }
        });
        addCall(call);
        mView.onFinish();
    }

    /**
     * @param notificationId notification id or "0" for reading all
     */
    @Override
    public void readNotification(String notificationId) {
        Call<Object> call = AppManager.getHttpService().readNotification(notificationId);
        addCall(call);
        call
                .enqueue(new BaseResponseCallback<Object>() {
                    @Override
                    protected void onSuccess(Object response) {
                        LogUtils.d(response);
                        mView.onReadSuccess();
                    }

                    @Override
                    protected void onFailure(@NonNull ErrorResponse errorResponse) {
                        LogUtils.d(errorResponse);
                        if (errorResponse.getCode() == ErrorResponse.STATUS_CODE_ERROR_UNKNOWN) {
                            mView.onReadSuccess(); // 成功的response body 为空，会走到onFail分支-_-||
                        }
                    }
                });
    }
}
