package com.sumian.hw.log;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.model.AppendObjectRequest;
import com.alibaba.sdk.android.oss.model.AppendObjectResult;
import com.alibaba.sdk.android.oss.model.ObjectMetadata;
import com.sumian.hw.network.callback.BaseResponseCallback;
import com.sumian.sleepdoctor.BuildConfig;
import com.sumian.sleepdoctor.app.HwAppManager;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by sm
 * on 2018/6/12 16:36
 * desc:
 **/
public class LogJobIntentService extends JobIntentService {

    private static final String TAG = LogJobIntentService.class.getSimpleName();

    /**
     * Unique job ID for this service.
     */
    static final int JOB_ID = 1001;

    /**
     * Convenience method for enqueuing work in to this service.
     */
    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, LogJobIntentService.class, JOB_ID, work);
    }


    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onHandleWork: publish log");
        }

        if (!isCanUpload()) {
            return;
        }

        Map<String, Object> map = new HashMap<>(0);
        map.put("suffix", "txt");

        Call<LogOssResponse> call = HwAppManager.getHwV1HttpService().autoUploadLog(map);

        call.enqueue(new BaseResponseCallback<LogOssResponse>() {
            @Override
            protected void onSuccess(LogOssResponse response) {
                if (isCanUpload()) {
                    File logFile = new File(getApplicationContext().getCacheDir(), LogManager.LOG_FILE_NAME);
                    requestOss(response, logFile.getAbsolutePath());
                }
            }

            @Override
            protected void onFailure(String error) {
            }
        });
    }

    private boolean isCanUpload() {
        return getLogFile().exists() && getLogFile().length() > 0;
    }

    private File getLogFile() {
        return new File(getApplicationContext().getCacheDir(), LogManager.LOG_FILE_NAME);
    }

    private void requestOss(LogOssResponse ossResponse, String filePath) {

        if (BuildConfig.DEBUG) {
            OSSLog.enableLog();
        }

        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(ossResponse.access_key_id, ossResponse.access_key_secret, ossResponse.security_token);
        OSSClient ossClient = new OSSClient(getApplicationContext(), ossResponse.endpoint, credentialProvider);
        // 构造上传请求

        AppendObjectRequest append = new AppendObjectRequest(ossResponse.bucket, ossResponse.object, filePath);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.addUserMetadata("Accept-Encoding", "");
        metadata.setContentType("application/octet-stream");

        append.setMetadata(metadata);

        append.setPosition(ossResponse.size);

        append.setProgressCallback((request, currentSize, totalSize) -> {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "log oss 服务文件上传中进度回调   currentSize = " + currentSize + "  totalSize = " + totalSize);
            }
        });

        ossClient.asyncAppendObject(append, new OSSCompletedCallback<AppendObjectRequest, AppendObjectResult>() {
            @SuppressWarnings("ResultOfMethodCallIgnored")
            @Override
            public void onSuccess(AppendObjectRequest request, AppendObjectResult result) {

                if (getLogFile().exists() && getLogFile().length() > 0) {
                    getLogFile().delete();
                }
                // Log.e(TAG, "onSuccess: ----2---->");
                // Log.e("AppendObject", "AppendSuccess");
                //Log.e("NextPosition", "" + result.getNextPosition());
            }

            @Override
            public void onFailure(AppendObjectRequest request, ClientException clientException, ServiceException serviceException) {
                //Log.e(TAG, "onFailure: ----------->");
                // 请求异常
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
            //前台程序
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (String activeProcess : processInfo.pkgList) {
                    if (activeProcess.equals(context.getPackageName())) {
                        isInBackground = false;
                    }
                }
            }
        }
        return isInBackground;
    }
}
