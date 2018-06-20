package com.sumian.sleepdoctor.leanCloud;

import android.content.Context;
import android.os.Build;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback;
import com.sumian.sleepdoctor.network.response.ErrorResponse;
import com.sumian.sleepdoctor.pager.activity.WelcomeActivity;

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

    public static void registerPushService(Context context) {
        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            public void done(AVException e) {
                if (e == null) {
                    String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
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
        AppManager
                .getHttpService()
                .uploadDeviceInfo("0", installationId, String.valueOf(Build.VERSION.SDK_INT))
                .enqueue(new BaseResponseCallback<Object>() {
                    @Override
                    protected void onSuccess(Object response) {

                    }

                    @Override
                    protected void onFailure(ErrorResponse errorResponse) {

                    }
                });
    }
}
