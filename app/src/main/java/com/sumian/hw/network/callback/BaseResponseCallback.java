package com.sumian.hw.network.callback;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.sumian.sleepdoctor.R;
import com.sumian.hw.account.activity.HwLoginActivity;
import com.sumian.hw.app.App;
import com.sumian.hw.app.ApplicationDelegate;
import com.sumian.hw.network.response.ErrorResponse;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jzz
 * on 2017/10/11.
 * desc:
 */

public abstract class BaseResponseCallback<T> implements Callback<T> {

    private static final String TAG = BaseResponseCallback.class.getSimpleName();

    private static final Handler mUiHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
        //runOnUiThread(() -> {
        onFinish();
        if (response.isSuccessful()) {
            T body = response.body();
            onSuccess(body);
        } else {
            ResponseBody errorBody = response.errorBody();
            if (errorBody == null) {
                onFailure(App.getAppContext().getString(R.string.error_request_failed_hint));
                return;
            }
            try {
                String errorJson = errorBody.string();
                ErrorResponse errorResponse = JSON.parseObject(errorJson, ErrorResponse.class);
                if (errorResponse == null) {
                    onFailure(App.getAppContext().getString(R.string.error_request_failed_hint));
                } else {
                    if (response.code() == 403) {
                        onForbidden(errorResponse.getMessage());
                    } else {
                        int statusCode = errorResponse.getStatus_code();
                        switch (statusCode) {
                            case 404:
                                onNotFound(errorResponse.getMessage());
                                break;
                            case 401://token 鉴权失败
                                if (!ApplicationDelegate.isIsLoginActivity()) {
                                    HwLoginActivity.show(App.getAppContext(), true);
                                    ApplicationDelegate.setIsLoginActivity(true);
                                }
                            default:
                                onFailure(errorResponse.getMessage());
                                break;
                        }
                    }
                }
            } catch (IOException e) {
                onFailure(App.getAppContext().getString(R.string.error_request_failed_hint));
                e.printStackTrace();
            }
        }
        // });
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        // runOnUiThread(() -> {
        onFinish();
        onFailure(App.getAppContext().getString(R.string.error_request_failed_hint));
        // });
        t.printStackTrace();
    }

    protected void onNotFound(String error) {

    }

    protected void onForbidden(String forbiddenError) {

    }

    protected abstract void onSuccess(T response);

    protected abstract void onFailure(String error);

    protected void onFinish() {
    }

    private void runOnUiThread(Runnable run) {
        Log.e(TAG, "runOnUiThread: -------->" + (Looper.myLooper() == Looper.getMainLooper()));
        if (Looper.myLooper() == Looper.getMainLooper()) {
            run.run();
        } else {
            mUiHandler.post(run);
        }
    }

}
