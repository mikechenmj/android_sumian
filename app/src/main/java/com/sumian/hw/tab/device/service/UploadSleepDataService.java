package com.sumian.hw.tab.device.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sumian.hw.app.HwAppManager;
import com.sumian.hw.network.callback.BaseResponseCallback;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

/**
 * Created by jzz
 * on 2017/11/22.
 * <p>
 * desc:
 */

public class UploadSleepDataService extends IntentService {

    private static final String TAG = UploadSleepDataService.class.getSimpleName();
    private int mCount;

    public UploadSleepDataService() {
        super(UploadSleepDataService.class.getSimpleName());
    }

    public static void startUploadService(Context context, String filePath, long
        receiveStartedTime, long receiveEndedTime) {
        Intent intent = new Intent(context, UploadSleepDataService.class);
        intent.putExtra("filePath", filePath);
        intent.putExtra("receiveStartedTime", receiveStartedTime);
        intent.putExtra("receiveEndedTime", receiveEndedTime);

        context.startService(intent);
        //Log.e(TAG, "startUploadService: ------->");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) return;
        String filePath = intent.getStringExtra("filePath");
        long receiveStartedTime = intent.getLongExtra("receiveStartedTime", 0);
        long receiveEndedTime = intent.getLongExtra("receiveEndedTime", 0);

        Log.e(TAG, "onHandleIntent: -------->startTime=" + receiveStartedTime + "  endTime=" +
            receiveEndedTime);

        File file = new File(filePath);
        if (!file.exists()) return;

        mCount++;
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        //final RequestBody requestFile = RequestBody.create(MediaType.parse("text/*"), file);

        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(),
            requestBody);

        MultipartBody.Part typePart = MultipartBody.Part.createFormData("type", "1");
        MultipartBody.Part receiveStartedTimePart = MultipartBody.Part.createFormData
            ("app_receive_started_at", receiveStartedTime + "");
        MultipartBody.Part receiveEndedTimePart = MultipartBody.Part.createFormData
            ("app_receive_ended_at", receiveEndedTime + "");

        Call<String> call = HwAppManager
            .getNetEngine()
            .getHttpService()
            .uploadRawData(typePart, receiveStartedTimePart, receiveEndedTimePart, filePart);

        call.enqueue(new BaseResponseCallback<String>() {
            @Override
            protected void onSuccess(String response) {
                //boolean delete = false;
                if (file.exists()) {
                    //  delete = file.delete();
                }
                Log.e(TAG, "onSuccess: --------->" + response);
                mCount = 0;

            }

            @Override
            protected void onFailure(String error) {
                if (mCount > 5) {
                    mCount = 0;
                    return;
                }
                onHandleIntent(intent);
            }

            @Override
            protected void onFinish() {

            }
        });

    }
}
