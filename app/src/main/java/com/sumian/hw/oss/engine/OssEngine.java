package com.sumian.hw.oss.engine;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.sumian.sleepdoctor.app.HwApp;
import com.sumian.hw.oss.bean.OssResponse;
import com.sumian.sleepdoctor.BuildConfig;
import com.sumian.sleepdoctor.account.bean.UserInfo;
import com.sumian.sleepdoctor.app.AppManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jzz
 * on 2018/1/2.
 * desc:
 */

public class OssEngine {

    private static final String TAG = OssEngine.class.getSimpleName();

    public OssEngine() {
        if (BuildConfig.DEBUG) OSSLog.enableLog();
    }

    public void uploadFile(OssResponse ossResponse, String localUploadFilePath) {
        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(ossResponse.getAccess_key_id(), ossResponse.getAccess_key_secret(), ossResponse.getSecurity_token());
        OSSClient ossClient = new OSSClient(HwApp.getAppContext(), ossResponse.getEndpoint(), credentialProvider);
        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(ossResponse.getBucket(), ossResponse.getObject(), localUploadFilePath);
        // 异步上传时可以设置进度回调

        Map<String, String> callbackParam = new HashMap<>();
        callbackParam.put("callbackUrl", ossResponse.getCallback_url());
        //callbackParam.put("callbackHost", "oss-cn-hangzhou.aliyuncs.com");
        //callbackParam.put("callbackBodyType", "application/json");//后台返回的 callback body 不是json数据,所以不能这么用
        callbackParam.put("callbackBody", ossResponse.getCallback_body());
        put.setCallbackParam(callbackParam);

        put.setProgressCallback((request, currentSize, totalSize) -> Log.i("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize));
        ossClient.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                String serverCallbackReturnBody = result.getServerCallbackReturnBody();
                Log.e(TAG, "onSuccess: --------->");
                String avatarUrl = null;
                if (TextUtils.isEmpty(serverCallbackReturnBody)) {
                    avatarUrl = "http://" + ossResponse.getBucket() + "." + ossResponse.getEndpoint() + "/" + ossResponse.getObject();
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(serverCallbackReturnBody);
                        avatarUrl = jsonObject.getString("avatar");
                        if (TextUtils.isEmpty(avatarUrl)) {
                            avatarUrl = "http://" + ossResponse.getBucket() + "." + ossResponse.getEndpoint() + "/" + ossResponse.getObject();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                UserInfo userInfo = AppManager.getAccountViewModel().getUserInfo();
                userInfo.setAvatar(avatarUrl);
                AppManager.getAccountViewModel().updateUserInfo(userInfo);
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                // 请求异常
                Log.e(TAG, "onFailure: --------->");
                // uploadFile(ossResponse, localUploadFilePath);
            }
        });
        // task.cancel(); // 可以取消任务
        // task.waitUntilFinished(); // 可以等待任务完成
    }
}
