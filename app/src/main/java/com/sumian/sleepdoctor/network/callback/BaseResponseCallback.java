package com.sumian.sleepdoctor.network.callback;

import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.app.App;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.network.response.ErrorResponse;

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

    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
        onFinish();
        if (response.isSuccessful()) {
            T body = response.body();
            onSuccess(body);
        } else {
            ResponseBody errorBody = response.errorBody();
            if (errorBody == null) {
                onFailure(App.Companion.getAppContext().getString(R.string.error_request_failed_hint));
                return;
            }
            try {
                String errorJson = errorBody.string();
                ErrorResponse errorResponse = JSON.parseObject(errorJson, ErrorResponse.class);
                if (errorResponse == null) {
                    onFailure(App.Companion.getAppContext().getString(R.string.error_request_failed_hint));
                } else {
                    int statusCode = errorResponse.status_code;
                    switch (statusCode) {
                        case 404:
                            onNotFound(errorResponse.toString());
                            break;
                        case 401://token 鉴权失败
                            AppManager.getAccountViewModel().updateTokenInvalidState(true);
                            break;
                        default:
                            onFailure(errorResponse.message);
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                onFailure(App.Companion.getAppContext().getString(R.string.error_request_failed_hint));
            }
        }
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        onFinish();
        onFailure(App.Companion.getAppContext().getString(R.string.error_request_failed_hint));
        t.printStackTrace();
    }

    protected void onNotFound(String error) {

    }

    protected abstract void onSuccess(T response);

    protected abstract void onFailure(String error);

    protected void onFinish() {
    }


}
