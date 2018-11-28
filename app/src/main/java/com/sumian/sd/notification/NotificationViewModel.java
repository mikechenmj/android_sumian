package com.sumian.sd.notification;

import com.blankj.utilcode.util.LogUtils;
import com.sumian.common.network.response.ErrorResponse;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.network.callback.BaseSdResponseCallback;
import com.sumian.sd.notification.bean.QueryNotificationResponse;

import org.jetbrains.annotations.NotNull;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
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
        Call<QueryNotificationResponse> call = AppManager.getSdHttpService().getNotificationList(1, 1);
        call.enqueue(new BaseSdResponseCallback<QueryNotificationResponse>() {
            @Override
            protected void onFailure(@NotNull ErrorResponse errorResponse) {
                LogUtils.d(errorResponse.getMessage());
            }

            @Override
            protected void onSuccess(QueryNotificationResponse response) {
                mUnreadCount.setValue(response.getMeta().getUnread_num());
            }
        });
    }
}
