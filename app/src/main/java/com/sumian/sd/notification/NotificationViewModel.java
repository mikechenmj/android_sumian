package com.sumian.sd.notification;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.network.callback.BaseResponseCallback;
import com.sumian.sd.network.response.ErrorResponse;
import com.sumian.sd.notification.bean.QueryNotificationResponse;

import retrofit2.Call;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/6 15:07
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class NotificationViewModel extends ViewModel {

    private MutableLiveData<Integer> mUnreadCount = new MutableLiveData<>();

    public NotificationViewModel() {
        updateUnreadCount();
    }

    public LiveData<Integer> getUnreadCount() {
        return mUnreadCount;
    }

    public void updateUnreadCount() {
        Call<QueryNotificationResponse> call = AppManager.getHttpService().getNotificationList(1, 1);
        call
                .enqueue(new BaseResponseCallback<QueryNotificationResponse>() {
                    @Override
                    protected void onSuccess(QueryNotificationResponse response) {
                        mUnreadCount.setValue(response.getMeta().getUnread_num());
                    }

                    @Override
                    protected void onFailure(int code, @NonNull String message) {
                        LogUtils.d(message);
                    }

                    @Override
                    protected void onFinish() {
                        super.onFinish();

                    }
                });
    }
}
