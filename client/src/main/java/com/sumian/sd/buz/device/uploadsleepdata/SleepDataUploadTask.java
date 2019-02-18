package com.sumian.sd.buz.device.uploadsleepdata;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.sumian.common.network.error.ErrorCode;
import com.sumian.common.network.response.ErrorResponse;
import com.sumian.common.utils.SumianExecutor;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.buz.device.AutoSyncDeviceDataUtil;
import com.sumian.sd.buz.report.bean.DailyReport;
import com.sumian.sd.common.log.LogManager;
import com.sumian.sd.common.network.callback.BaseSdResponseCallback;
import com.sumian.sd.common.oss.OssEngine;
import com.sumian.sd.common.oss.OssResponse;
import com.sumian.sd.common.oss.hwbean.OssTransData;
import com.sumian.sd.common.oss.hwbean.OssTransDataError;
import com.sumian.sd.common.utils.NetUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
        LogManager.appendTransparentLog("1.开始请求透传数据的 oss  凭证" + "， fileName: " + fileName);
        Call<OssResponse> call = AppManager.getSdHttpService().uploadTransData(map);
        call.enqueue(new BaseSdResponseCallback<OssResponse>() {
            @Override
            protected void onFailure(@NotNull ErrorResponse errorResponse) {
                boolean isSuccess = false;
                if (errorResponse.getCode() == ErrorCode.FORBIDDEN) {
                    LogManager.appendTransparentLog("2.该组透传数据已存在服务器,403 禁止再次上传 error=" + errorResponse.getMessage() + "， fileName: " + fileName);
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
        LogManager.appendTransparentLog("该组透传数据开始发起 oss 异步文件上传任务....");
        OssEngine.Companion.uploadFile(ossResponse, filePath, new OssEngine.UploadCallback() {
            @Override
            public void onSuccess(@org.jetbrains.annotations.Nullable String response) {
                if (response == null) {
                    return;
                }
                LogManager.appendTransparentLog("oss onSuccess" + response);
                if (trasDataType == 0x01) {
                    AutoSyncDeviceDataUtil.INSTANCE.saveAutoSyncTime();
                }
                boolean success = false;
                boolean retry = true;
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (response.contains("data")) {
                            //透传成功睡眠特征数据,并解析成功
                            LogManager.appendTransparentLog("该组透传数据 oss服务上传成功, 是睡眠特征数据-->" + " dailyReports=" + JSON.parseArray(jsonObject.getString("data"), DailyReport.class).toString());
                            success = true;
                            retry = false;
                        } else if (response.contains("errors")) {//透传成功睡眠特征数据,但是出现错误信息.比如采集时间重叠  解析失败  文件名存在
                            //{"errors":{"code":1,"user_message":"采集时间重叠","internal_message":"coverage","more_info":""}}
                            OssTransDataError ossTransDataError = JSON.parseObject(jsonObject.getString("errors"), OssTransDataError.class);
                            LogManager.appendTransparentLog("该组透传数据 oss服务上传成功, 但出现错误信息  ossTransDataError=" + ossTransDataError.toString());
                            switch (ossTransDataError.getCode()) {
                                case 1: // 采集时间重叠
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
                            LogManager.appendTransparentLog("该组透传数据 oss服务上传成功,不是睡眠特征数据---->" + " ossTransData=" + JSON.parseObject(response, OssTransData.class).toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                onUploadFinish(success, retry);
            }

            @Override
            public void onFailure(@org.jetbrains.annotations.Nullable String errorCode, @org.jetbrains.annotations.Nullable String message) {
                Log.e(TAG, "onFailure: ----------->");
                LogManager.appendUserOperationLog("该组透传数据 oss 上传失败,进入队列末尾进行再次上传  clientException=" + message);
                onUploadFinish(false);
            }
        }, (currentSize, totalSize) -> LogManager.appendTransparentLog("该组透传数据oss 服务文件上传中进度回调   currentSize = " + currentSize + "  totalSize = " + totalSize));
    }

    @Override
    public SleepDataUploadTask clone() throws CloneNotSupportedException {
        return (SleepDataUploadTask) super.clone();
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

    public interface TaskCallback {
        void executeTaskFinish(SleepDataUploadTask sleepDataUploadTask, boolean isSuccess, boolean retry, @Nullable String message);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof SleepDataUploadTask)) {
            return false;
        } else {
            return this.filePath.equals(((SleepDataUploadTask) obj).filePath);
        }
    }
}
