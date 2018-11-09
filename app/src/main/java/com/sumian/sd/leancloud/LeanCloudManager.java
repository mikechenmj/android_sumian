package com.sumian.sd.leancloud;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMClientEventHandler;
import com.blankj.utilcode.util.LogUtils;
import com.sumian.common.network.response.ErrorResponse;
import com.sumian.sd.BuildConfig;
import com.sumian.sd.app.App;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.main.WelcomeActivity;
import com.sumian.sd.network.callback.BaseSdResponseCallback;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/7 16:08
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class LeanCloudManager {

    private static final String TAG = LeanCloudManager.class.getSimpleName();

    public static void init(Context context) {
        PushService.setDefaultChannelId(context, "push_channel");
        AVOSCloud.initialize(context, BuildConfig.LEANCLOUD_APP_ID, BuildConfig.LEANCLOUD_APP_KEY);
        AVOSCloud.setDebugLogEnabled(BuildConfig.DEBUG);
        AVIMClient.setAutoOpen(false);
        AVIMClient.setClientEventHandler(new AVIMClientEventHandler() {
            @Override
            public void onConnectionPaused(AVIMClient avimClient) {
                Log.e(TAG, "onConnectionPaused: ----------->");
            }

            @Override
            public void onConnectionResume(AVIMClient avimClient) {
                Log.e(TAG, "onConnectionResume: ------------->");
            }

            @Override
            public void onClientOffline(AVIMClient avimClient, int i) {
                Log.e(TAG, "onClientOffline: ------------------->");
            }
        });
        //设置后台自动重启
        PushService.setAutoWakeUp(true);
        // 设置默认打开的 Activity
        PushService.setDefaultPushCallback(context, WelcomeActivity.class);
    }

    public static void uploadPushId() {
        PushService.subscribe(App.getAppContext(), "app-sd", null);
        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
                    uploadDeviceInfo(installationId);
                }
            }
        });
    }

    private static void uploadDeviceInfo(String installationId) {
        LogUtils.d("installationId", installationId);
        Call<Object> call = AppManager
                .getSdHttpService()
                .uploadDeviceInfo("0", installationId, String.valueOf(Build.VERSION.SDK_INT));
        call.enqueue(new BaseSdResponseCallback<Object>() {
            @Override
            protected void onFailure(@NotNull ErrorResponse errorResponse) {
                LogUtils.d(errorResponse.getMessage());
            }

            @Override
            protected void onSuccess(Object response) {
                LogUtils.d(response);
            }

        });
    }
}
