package com.sumian.sleepdoctor.leancloud;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMClientEventHandler;
import com.blankj.utilcode.util.LogUtils;
import com.sumian.sleepdoctor.BuildConfig;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.main.WelcomeActivity;
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback;
import com.sumian.sleepdoctor.network.response.ErrorResponse;

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

    public static void registerPushService(Context context) {
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


        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
                    LogUtils.d("LeanCloud push installationId", installationId);
                    uploadDeviceInfo(installationId);
                }
            }
        });
        //设置后台自动重启
        PushService.setAutoWakeUp(true);
        // 设置默认打开的 Activity
        PushService.setDefaultPushCallback(context, WelcomeActivity.class);
    }

    private static void uploadDeviceInfo(String installationId) {
        Call<Object> call = AppManager
                .getHttpService()
                .uploadDeviceInfo("0", installationId, String.valueOf(Build.VERSION.SDK_INT));
        call
                .enqueue(new BaseResponseCallback<Object>() {
                    @Override
                    protected void onSuccess(Object response) {

                    }

                    @Override
                    protected void onFailure(@NonNull ErrorResponse errorResponse) {

                    }
                });
    }
}
