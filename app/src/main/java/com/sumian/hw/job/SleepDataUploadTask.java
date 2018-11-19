package com.sumian.hw.job;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.model.ObjectMetadata;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.sumian.common.network.error.ErrorCode;
import com.sumian.common.network.response.ErrorResponse;
import com.sumian.common.utils.SumianExecutor;
import com.sumian.hw.log.LogManager;
import com.sumian.hw.oss.bean.OssResponse;
import com.sumian.hw.oss.bean.OssTransData;
import com.sumian.hw.oss.bean.OssTransDataError;
import com.sumian.hw.report.bean.DailyReport;
import com.sumian.hw.utils.NetUtil;
import com.sumian.sd.BuildConfig;
import com.sumian.sd.app.App;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.device.AutoSyncDeviceDataUtil;
import com.sumian.sd.network.callback.BaseSdResponseCallback;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by jzz
 * on 2017/12/19.
 * desc:
 */

@SuppressWarnings("UnusedReturnValue")
public class SleepDataUploadTask implements Serializable, Cloneable {

    private static final String TAG = SleepDataUploadTask.class.getSimpleName();
    String filePath;//文件保存路径
    private String beginCmd;
    private String endCmd;
    private String monitorSn;
    private String speedSleeperSn;
    private long receiveStartedTime;//开始接收设备睡眠特征时间戳
    private long receiveEndedTime;//接收设备睡眠特征结束时间戳
    private int type;//透传数据的类型
    private transient TaskCallback mTaskCallback;

    SleepDataUploadTask(String filePath, String beginCmd, String endCmd, String monitorSn, String speedSleeperSn, long receiveStartedTime, long receiveEndedTime, int type) {
        this.filePath = filePath;
        this.beginCmd = beginCmd;
        this.endCmd = endCmd;
        this.monitorSn = monitorSn;
        this.speedSleeperSn = speedSleeperSn;
        this.receiveStartedTime = receiveStartedTime;
        this.receiveEndedTime = receiveEndedTime;
        this.type = type;
    }

    public SleepDataUploadTask setType(int type) {
        this.type = type;
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        return "SleepDataUploadTask{" +
                "filePath='" + filePath + '\'' +
                ", beginCmd='" + beginCmd + '\'' +
                ", endCmd='" + endCmd + '\'' +
                ", receiveStartedTime=" + receiveStartedTime +
                ", receiveEndedTime=" + receiveEndedTime +
                ", type=" + type +
                '}';
    }

    SleepDataUploadTask setTaskCallback(TaskCallback taskCallback) {
        mTaskCallback = taskCallback;
        return this;
    }

    void execute() {
        //执行任务,首先缓存到本地文件,然后进行透传
        if (!TextUtils.isEmpty(filePath)) {
            if (!NetUtil.hasInternet()) {//没有网络直接返回任务执行失败,使任务进入队列末尾排队等待
                onUploadFinish(false);
            } else {
                requestOssToken(new File(filePath).getName(), monitorSn, speedSleeperSn, type, receiveStartedTime, receiveEndedTime);
            }
        }
    }

    private void requestOssToken(String fileName, String monitorSn, String sleeperSn, int transDataType, long receiveStartedTime, long receiveEndedTime) {
        Map<String, Object> map = new HashMap<>(0);
        map.put("filename", fileName);
        if (!TextUtils.isEmpty(monitorSn)) {
            map.put("sn", monitorSn);
        }
        if (!TextUtils.isEmpty(sleeperSn)) {
            map.put("sleeper_sn", sleeperSn);
        }
        map.put("type", transDataType);
        map.put("app_receive_started_at", receiveStartedTime);
        map.put("app_receive_ended_at", receiveEndedTime);
        LogManager.appendTransparentLog("1.开始请求透传数据的 oss  凭证");
        Call<OssResponse> call = AppManager.getSdHttpService().uploadTransData(map);
        call.enqueue(new BaseSdResponseCallback<OssResponse>() {
            @Override
            protected void onFailure(@NotNull ErrorResponse errorResponse) {
                boolean isSuccess = false;
                if (errorResponse.getCode() == ErrorCode.FORBIDDEN) {
                    LogManager.appendTransparentLog("2.该组透传数据已存在服务器,403 禁止再次上传 error=" + errorResponse.getMessage());
                    if (transDataType == 1) {
                        AutoSyncDeviceDataUtil.INSTANCE.saveAutoSyncTime();
                    }
                    // 该组透传数据已存在服务器, 当做成功
                    isSuccess = true;
                } else {
                    LogManager.appendTransparentLog("2.该组透传数据请求服务器获取 OssResponse 令牌失败,进入队列末尾等待重新上传  错误信息=" + errorResponse.getMessage());
                }
                onUploadFinish(isSuccess);
            }

            @Override
            protected void onSuccess(OssResponse ossResponse) {
                LogManager.appendTransparentLog("2.该组透传数据请求服务器获取 OssResponse 成功,准备进行 oss 服务进行上传文件...");
                requestOss(ossResponse, filePath, transDataType);
            }
        });
    }

    private void requestOss(OssResponse ossResponse, String filePath, int trasDataType) {
        if (BuildConfig.DEBUG) {
            OSSLog.enableLog();
        }
        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(ossResponse.getAccess_key_id(), ossResponse.getAccess_key_secret(), ossResponse.getSecurity_token());
        OSSClient ossClient = new OSSClient(App.Companion.getAppContext(), ossResponse.getEndpoint(), credentialProvider);
        PutObjectRequest putObjectRequest = new PutObjectRequest(ossResponse.getBucket(), ossResponse.getObject(), filePath);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.addUserMetadata("Accept-Encoding", "");
        putObjectRequest.setMetadata(metadata);
        Map<String, String> callbackParam = new HashMap<>(0);
        callbackParam.put("callbackUrl", ossResponse.getCallback_url());
        callbackParam.put("callbackBody", ossResponse.getCallback_body());
        putObjectRequest.setCallbackParam(callbackParam);
        putObjectRequest.setProgressCallback((request, currentSize, totalSize) -> LogManager.appendTransparentLog("该组透传数据oss 服务文件上传中进度回调   currentSize = " + currentSize + "  totalSize = " + totalSize));
        LogManager.appendTransparentLog("该组透传数据开始发起 oss 异步文件上传任务....");
        ossClient.asyncPutObject(putObjectRequest, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @SuppressWarnings({"unchecked", "SingleStatementInBlock"})
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                String returnBody = result.getServerCallbackReturnBody();
                LogManager.appendTransparentLog("oss onSuccess" + returnBody);
                if (trasDataType == 0x01) {
                    AutoSyncDeviceDataUtil.INSTANCE.saveAutoSyncTime();
                }
                boolean success = false;
                boolean retry = true;
                if (!TextUtils.isEmpty(returnBody)) {
                    try {
                        JSONObject jsonObject = new JSONObject(returnBody);
                        if (returnBody.contains("data")) {
                            //透传成功睡眠特征数据,并解析成功
                            LogManager.appendTransparentLog("该组透传数据 oss服务上传成功, 是睡眠特征数据-->" + " dailyReports=" + JSON.parseArray(jsonObject.getString("data"), DailyReport.class).toString());
                            success = true;
                            retry = false;
                        } else if (returnBody.contains("errors")) {//透传成功睡眠特征数据,但是出现错误信息.比如采集时间重叠  解析失败  文件名存在
                            //{"errors":{"code":1,"user_message":"采集时间重叠","internal_message":"coverage","more_info":""}}
                            OssTransDataError ossTransDataError = JSON.parseObject(jsonObject.getString("errors"), OssTransDataError.class);
                            LogManager.appendTransparentLog("该组透传数据 oss服务上传成功, 但出现错误信息  ossTransDataError=" + ossTransDataError.toString());
                            switch (ossTransDataError.getCode()) {
                                case 1: // 采集时间重叠
                                    //noinspection ConstantConditions
                                    success = false;
                                    retry = false;
                                    break;
                                case 3://服务器, 透传数据的文件名存在，当成功处理
                                    success = true;
                                    retry = false;
                                    break;
                                default:
                                    break;
                            }
                        } else {
                            //透传成功,不是睡眠特征数据
                            LogManager.appendTransparentLog("该组透传数据 oss服务上传成功,不是睡眠特征数据---->" + " ossTransData=" + JSON.parseObject(returnBody, OssTransData.class).toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                onUploadFinish(success, retry);
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                Log.e(TAG, "onFailure: ----------->");
                // 请求异常
                if (clientException != null) {
                    LogManager.appendUserOperationLog("该组透传数据 oss 上传失败,进入队列末尾进行再次上传  clientException=" + clientException.getLocalizedMessage());
                }
                if (serviceException != null) {
                    LogManager.appendUserOperationLog("该组透传数据 oss 上传失败,进入队列末尾进行再次上传  serviceException=" + serviceException.getLocalizedMessage());
                }
                onUploadFinish(false);
            }
        });
    }

    @Override
    public SleepDataUploadTask clone() throws CloneNotSupportedException {
        return (SleepDataUploadTask) super.clone();
    }

    public interface TaskCallback {
        void executeTaskFinish(SleepDataUploadTask sleepDataUploadTask, boolean isSuccess, boolean retry, @Nullable String message);
    }

    /**
     * 默认情况下上传成功 删除任务，失败重传
     *
     * @param isSuccess 上传成功与否
     */
    private void onUploadFinish(boolean isSuccess) {
        onUploadFinish(isSuccess, !isSuccess);
    }

    /**
     * 默认情况下上传成功 删除任务，失败重传。
     * 某些特殊情况下（例如文件已存在），上传失败，不重传
     *
     * @param isSuccess 是否上传成功
     * @param retry     是否重试
     */
    private void onUploadFinish(boolean isSuccess, boolean retry) {
        SumianExecutor.INSTANCE.runOnUiThread(() -> mTaskCallback.executeTaskFinish(SleepDataUploadTask.this, isSuccess, retry, null));
    }
}
