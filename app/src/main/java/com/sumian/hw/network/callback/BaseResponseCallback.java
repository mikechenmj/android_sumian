package com.sumian.hw.network.callback;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.sumian.hw.account.activity.HwLoginActivity;
import com.sumian.hw.network.response.ErrorResponse;
import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.app.App;
import com.sumian.sleepdoctor.app.HwApplicationDelegate;
import com.sumian.sleepdoctor.utils.JsonUtil;

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
                errorUnknown();
                return;
            }
            try {
                String errorJson = errorBody.string();
                ErrorResponse errorResponse = JsonUtil.fromJson(errorJson, ErrorResponse.class);
                if (errorResponse == null) {
                    errorUnknown();
                } else {
                    int statusCode = errorResponse.getCode();
                    if (statusCode == ErrorCode.UNAUTHORIZED) {
                        if (!HwApplicationDelegate.isIsLoginActivity()) {
                            HwLoginActivity.show(App.Companion.getAppContext(), true);
                            HwApplicationDelegate.setIsLoginActivity(true);
                        }
                    } else {
                        onFailure(errorResponse.getCode(), errorResponse.getMessage());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                errorUnknown();
            }
        }
    }

    private void errorUnknown() {
        onFailure(ErrorCode.UNKNOWN, App.Companion.getAppContext().getString(R.string.error_request_failed_hint));
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        onFinish();
        errorUnknown();
        t.printStackTrace();
    }

    protected abstract void onSuccess(T response);

    protected abstract void onFailure(int code, String message);

    protected void onFinish() {
    }
}
