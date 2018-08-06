package com.sumian.hw.network.response;

import android.text.TextUtils;

/**
 * Created by jzz
 * on 2017/9/30
 * <p>
 * desc:
 */

public class ErrorResponse {

    private int status_code;
    private String message;
    private ErrorInfo error;

    public int getStatus_code() {
        return status_code;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }

    public String getMessage() {
        if (!TextUtils.isEmpty(message)) {
            return message;
        }
        if (error != null) {
            return error.getUser_message();
        }
        return "Error unknown";
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ErrorInfo getError() {
        return error;
    }

    public void setError(ErrorInfo error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "status_code=" + status_code +
                ", message='" + message + '\'' +
                '}';
    }

}
