package com.sumian.hw.log;

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
import com.blankj.utilcode.util.LogUtils;
import com.sumian.hw.network.callback.BaseResponseCallback;
import com.sumian.sd.BuildConfig;
import com.sumian.sd.account.bean.Token;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.utils.AppUtil;

import java.io.File;
import java.util.HashMap;
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

    public static void uploadLogIfNeed(Context context) {
        if (!AppUtil.isAppForeground()) {
            enqueueWork(context, LogJobIntentService.class, JOB_ID, new Intent(context, LogJobIntentService.class));
        }
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onHandleWork: publish log");
        }
        if (!isCanUpload()) {
            return;
        }
        Token token = AppManager.getAccountViewModel().getToken();
        if (token == null) {
            return;
        }
        Map<String, Object> map = new HashMap<>(0);
        map.put("suffix", "txt");
        Call<LogOssResponse> call = AppManager.getHwV1HttpService().autoUploadLog(map);
        call.enqueue(new BaseResponseCallback<LogOssResponse>() {
            @Override
            protected void onSuccess(LogOssResponse response) {
                LogUtils.d(response);
                if (isCanUpload()) {
                    File logFile = new File(getApplicationContext().getCacheDir(), LogManager.LOG_FILE_NAME);
                    uploadLog(response, logFile.getAbsolutePath());
                }
            }

            @Override
            protected void onFailure(int code, String error) {
                LogUtils.d(error);
            }

            @Override
            protected void onUnauthorized() {
                LogUtils.d("Token不合法，无法上传Log");
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private boolean isCanUpload() {
        return getLogFile().exists() && getLogFile().length() > 0;
    }

    private File getLogFile() {
        return new File(getApplicationContext().getCacheDir(), LogManager.LOG_FILE_NAME);
    }

    private void uploadLog(LogOssResponse ossResponse, String filePath) {
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
                LogUtils.d(request, result);
                if (getLogFile().exists() && getLogFile().length() > 0) {
                    getLogFile().delete();
                }
            }

            @Override
            public void onFailure(AppendObjectRequest request, ClientException clientException, ServiceException serviceException) {
                LogUtils.d(clientException, serviceException);
            }
        });
    }
}
