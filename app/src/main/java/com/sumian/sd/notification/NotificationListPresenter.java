package com.sumian.sd.notification;

import com.blankj.utilcode.util.LogUtils;
import com.sumian.common.network.error.ErrorCode;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.network.callback.BaseSdResponseCallback;
import com.sumian.sd.notification.bean.Notification;
import com.sumian.sd.notification.bean.QueryNotificationResponse;

import org.jetbrains.annotations.NotNull;

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
    private static final int INIT_PAGE = 1;
    private NotificationListContract.View mView;
    private int mPage = INIT_PAGE;

    NotificationListPresenter(NotificationListContract.View view) {
        mView = view;
    }

    @Override
    public void loadData(boolean isInitLoad) {
        mView.onBegin();
        if (isInitLoad) {
            mPage = INIT_PAGE;
        }
        Call<QueryNotificationResponse> call = AppManager.getSdHttpService().getNotificationList(mPage, PER_PAGE);
        addCall(call);
        call.enqueue(new BaseSdResponseCallback<QueryNotificationResponse>() {
            @Override
            protected void onFailure(@NotNull com.sumian.common.network.response.ErrorResponse errorResponse) {
                LogUtils.d(errorResponse.getMessage());
                mView.onLoadMore(null, errorResponse.getCode() == ErrorCode.NOT_FOUND);
            }

            @Override
            protected void onSuccess(QueryNotificationResponse response) {
                LogUtils.d(response);
                List<Notification> data = response.getData();
                mView.onLoadMore(data, data.size() == PER_PAGE);
                mPage++;
            }

        });
        addCall(call);
        mView.onFinish();
    }

    /**
     * @param notificationId notification id or "0" for reading all
     */
    @Override
    public void readNotification(String notificationId, String notificationDataId) {
        Call<Object> call = AppManager.getSdHttpService().readNotification(notificationId, notificationDataId);
        addCall(call);
        call.enqueue(new BaseSdResponseCallback<Object>() {
            @Override
            protected void onFailure(@NotNull com.sumian.common.network.response.ErrorResponse errorResponse) {
                LogUtils.d(errorResponse.getMessage());
                if (errorResponse.getCode() == ErrorCode.STATUS_CODE_ERROR_UNKNOWN) {
                    mView.onReadSuccess(); // 成功的response body 为空，会走到onFail分支-_-||
                }
            }

            @Override
            protected void onSuccess(Object response) {
                LogUtils.d(response);
                mView.onReadSuccess();
            }

        });
    }
}
