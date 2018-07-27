package com.sumian.app.job;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
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
import com.sumian.app.BuildConfig;
import com.sumian.app.app.App;
import com.sumian.app.app.AppManager;
import com.sumian.app.common.util.NetUtil;
import com.sumian.app.common.util.SpUtil;
import com.sumian.app.improve.report.dailyreport.DailyReport;
import com.sumian.app.log.LogManager;
import com.sumian.app.network.callback.BaseResponseCallback;
import com.sumian.app.oss.bean.OssResponse;
import com.sumian.app.oss.bean.OssTransData;
import com.sumian.app.oss.bean.OssTransDataError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by jzz
 * on 2017/12/19.
 * desc:
 */

@SuppressWarnings("UnusedReturnValue")
public class JobTask implements Serializable, Cloneable {

    private static final String TAG = JobTask.class.getSimpleName();
    public static final String ACTION_SYNC = "com.sumian.app.action.SYNC";
    public static final String EXTRA_SYNC_STATUS = "com.sumian.app.extra.SYNC_STATUS";

    private String filePath;//文件保存路径
    private String beginCmd;
    private String endCmd;
    private String monitorSn;
    private String speedSleeperSn;
    private long receiveStartedTime;//开始接收设备睡眠特征时间戳
    private long receiveEndedTime;//接收设备睡眠特征结束时间戳
    private int type;//透传数据的类型

    private transient TaskCallback mTaskCallback;

//    public JobTask() {
//    }

    JobTask(String filePath, String beginCmd, String endCmd, String monitorSn, String speedSleeperSn, long receiveStartedTime, long receiveEndedTime, int type) {
        this.filePath = filePath;
        this.beginCmd = beginCmd;
        this.endCmd = endCmd;
        this.monitorSn = monitorSn;
        this.speedSleeperSn = speedSleeperSn;
        this.receiveStartedTime = receiveStartedTime;
        this.receiveEndedTime = receiveEndedTime;
        this.type = type;
    }

    public JobTask setType(int type) {
        this.type = type;
        return this;
    }

    @Override
    public String toString() {
        return "JobTask{" +
            "filePath='" + filePath + '\'' +
            ", beginCmd='" + beginCmd + '\'' +
            ", endCmd='" + endCmd + '\'' +
            ", receiveStartedTime=" + receiveStartedTime +
            ", receiveEndedTime=" + receiveEndedTime +
            ", type=" + type +
            ", mTaskCallback=" + mTaskCallback +
            '}';
    }

    public JobTask setTaskCallback(TaskCallback taskCallback) {
        mTaskCallback = taskCallback;
        return this;
    }

    public void execute() {
        //执行任务,首先缓存到本地文件,然后进行透传
        if (!TextUtils.isEmpty(filePath)) {
            if (!NetUtil.hasInternet()) {//没有网络直接返回任务执行失败,使任务进入队列末尾排队等待
                mTaskCallback.executeCallbackFailed(this);
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
        Call<OssResponse> call = AppManager.getV1HttpService().uploadTransData(map);
        call.enqueue(new BaseResponseCallback<OssResponse>() {
            @Override
            protected void onSuccess(OssResponse ossResponse) {
                LogManager.appendTransparentLog("2.该组透传数据请求服务器获取 OssResponse 成功,准备进行 oss 服务进行上传文件...");
                requestOss(ossResponse, filePath);
            }

            @Override
            protected void onFailure(String error) {
                LogManager.appendTransparentLog("2.该组透传数据请求服务器获取 OssResponse 令牌失败,进入队列末尾等待重新上传  错误信息=" + error);
                mTaskCallback.executeCallbackFailed(JobTask.this);
            }

            @Override
            protected void onForbidden(String forbiddenError) {
                super.onForbidden(forbiddenError);
                Intent intent = new Intent(JobTask.ACTION_SYNC);
                intent.putExtra(JobTask.EXTRA_SYNC_STATUS, true);
                LocalBroadcastManager.getInstance(App.getAppContext()).sendBroadcast(intent);
                LogManager.appendTransparentLog("2.该组透传数据已存在服务器,403 禁止再次上传 error=" + forbiddenError);

                SpUtil.initEdit("upload_sleep_cha_time").putLong("time", System.currentTimeMillis()).apply();

                mTaskCallback.executeCallbackSuccess(JobTask.this);
            }
        });
    }

    private void requestOss(OssResponse ossResponse, String filePath) {
        if (BuildConfig.DEBUG) {
            OSSLog.enableLog();
        }

        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(ossResponse.getAccess_key_id(), ossResponse.getAccess_key_secret(), ossResponse.getSecurity_token());
        OSSClient ossClient = new OSSClient(App.getAppContext(), ossResponse.getEndpoint(), credentialProvider);
        // 构造上传请求

        PutObjectRequest putObjectRequest = new PutObjectRequest(ossResponse.getBucket(), ossResponse.getObject(), filePath);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.addUserMetadata("Accept-Encoding", "");
        putObjectRequest.setMetadata(metadata);

        // 异步上传时可以设置进度回调
        Map<String, String> callbackParam = new HashMap<>(0);
        callbackParam.put("callbackUrl", ossResponse.getCallback_url());
        //callbackParam.put("callbackHost", "oss-cn-hangzhou.aliyuncs.com");
        //callbackParam.put("callbackBodyType", "application/json");//如果加入该请求参数,会出现请求500的错误.直接
        callbackParam.put("callbackBody", ossResponse.getCallback_body());
        putObjectRequest.setCallbackParam(callbackParam);

        putObjectRequest.setProgressCallback((request, currentSize, totalSize) -> LogManager.appendTransparentLog("该组透传数据oss 服务文件上传中进度回调   currentSize = " + currentSize + "  totalSize = " + totalSize));

        LogManager.appendTransparentLog("该组透传数据开始发起 oss 异步文件上传任务....");
        ossClient.asyncPutObject(putObjectRequest, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @SuppressWarnings("unchecked")
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.e(TAG, "onSuccess: -------->");
                String returnBody = result.getServerCallbackReturnBody();

                Intent intent = new Intent(JobTask.ACTION_SYNC);
                intent.putExtra(JobTask.EXTRA_SYNC_STATUS, true);
                LocalBroadcastManager.getInstance(App.getAppContext()).sendBroadcast(intent);

                SpUtil.initEdit("upload_sleep_cha_time").putLong("time", System.currentTimeMillis()).apply();

                if (!TextUtils.isEmpty(returnBody)) {
                    try {
                        JSONObject jsonObject = new JSONObject(returnBody);
                        if (returnBody.contains("data")) {
                            //透传成功睡眠特征数据,并解析成功
                            String data = jsonObject.getString("data");
                            List<DailyReport> dailyReports = JSON.parseArray(data, DailyReport.class);
                            AppManager.getReportModel().setCacheDailyReport(dailyReports.get(0));
                            LogManager.appendTransparentLog("该组透传数据 oss服务上传成功--是睡眠特征数据-->" + " dailyReports=" + dailyReports.toString());
                        } else if (returnBody.contains("errors")) {//透传成功睡眠特征数据,但是出现错误信息.比如采集时间重叠  解析失败  文件名存在
                            String errors = jsonObject.getString("errors");
                            OssTransDataError ossTransDataError = JSON.parseObject(errors, OssTransDataError.class);
                            LogManager.appendTransparentLog("该组透传数据 oss服务上传成功--但出现错误信息  ossTransDataError=" + ossTransDataError.toString());

                            AppManager.getReportModel().notifySyncStatus(0xff);

                        } else {
                            //透传成功,不是睡眠特征数据
                            OssTransData ossTransData = JSON.parseObject(returnBody, OssTransData.class);
                            LogManager.appendTransparentLog("该组透传数据oss服务上传成功--不是睡眠特征数据---->" + " ossTransData=" + ossTransData.toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    LogManager.appendTransparentLog("oss success response=" + returnBody);
                }

                mTaskCallback.executeCallbackSuccess(JobTask.this);
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                Log.e(TAG, "onFailure: ----------->");
                // 请求异常
                Intent intent = new Intent(JobTask.ACTION_SYNC);
                intent.putExtra(JobTask.EXTRA_SYNC_STATUS, false);
                LocalBroadcastManager.getInstance(App.getAppContext()).sendBroadcast(intent);

                if (clientException != null) {
                    LogManager.appendUserOperationLog("该组透传数据 oss 上传失败,进入队列末尾进行再次上传  clientException=" + clientException.getLocalizedMessage());
                }

                if (serviceException != null) {
                    LogManager.appendUserOperationLog("该组透传数据 oss 上传失败,进入队列末尾进行再次上传  serviceException=" + serviceException.getLocalizedMessage());
                }

                mTaskCallback.executeCallbackFailed(JobTask.this);
            }
        });

    }

    @Override
    public JobTask clone() throws CloneNotSupportedException {
        return (JobTask) super.clone();
    }

    public interface TaskCallback {

        void executeCallbackFailed(JobTask jobTask);

        void executeCallbackSuccess(JobTask jobTask);

    }

}
