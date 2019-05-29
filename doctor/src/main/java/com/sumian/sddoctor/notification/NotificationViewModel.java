package com.sumian.sddoctor.notification;

import com.blankj.utilcode.util.LogUtils;
import com.sumian.common.buz.notification.NotificationListResponse;
import com.sumian.common.network.response.ErrorResponse;
import com.sumian.sddoctor.app.AppManager;
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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

    public LiveData<Integer> getUnreadCountLiveData() {
        return mUnreadCount;
    }

    public void updateUnreadCount() {
        AppManager.getHttpService().getNotificationList(1, 10, "unread")
                .enqueue(new BaseSdResponseCallback<NotificationListResponse>() {
                    @Override
                    protected void onSuccess(NotificationListResponse response) {
                        mUnreadCount.setValue(response.getMeta().getUnreadNum());
                    }

                    @Override
                    protected void onFailure(@NonNull ErrorResponse errorResponse) {
                        LogUtils.d(errorResponse);
                    }

                    @Override
                    protected void onFinish() {
                        super.onFinish();

                    }
                });
    }
}
