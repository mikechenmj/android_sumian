package com.sumian.sleepdoctor.notification;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback;
import com.sumian.sleepdoctor.network.response.ErrorResponse;
import com.sumian.sleepdoctor.notification.bean.QueryNotificationResponse;

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
        AppManager.getHttpService().getNotificationList(1, 1)
                .enqueue(new BaseResponseCallback<QueryNotificationResponse>() {
                    @Override
                    protected void onSuccess(QueryNotificationResponse response) {
                        mUnreadCount.setValue(response.getMeta().getUnread_num());
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
