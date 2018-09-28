package com.sumian.sd.notification;

import android.support.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.sumian.common.network.error.ErrorCode;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.network.callback.BaseResponseCallback;
import com.sumian.sd.network.response.ErrorResponse;
import com.sumian.sd.notification.bean.Notification;
import com.sumian.sd.notification.bean.QueryNotificationResponse;

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
            protected void onFailure(int code, @NonNull String message) {
                LogUtils.d(message);
                mView.onLoadMore(null, code == ErrorCode.NOT_FOUND);
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
                    protected void onFailure(int code, @NonNull String message) {
                        LogUtils.d(message);
                        if (code == ErrorResponse.STATUS_CODE_ERROR_UNKNOWN) {
                            mView.onReadSuccess(); // 成功的response body 为空，会走到onFail分支-_-||
                        }
                    }
                });
    }
}
