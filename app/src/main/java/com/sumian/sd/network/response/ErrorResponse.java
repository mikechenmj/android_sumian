package com.sumian.sd.network.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jzz
 * on 2017/9/30
 * <p>
 * desc:
 */

public class ErrorResponse {
    @SerializedName("status_code")
    private int code;
    private String message;
    // 兼容性数据，有的网络请求有error info，有个没有
    private ErrorInfo error;

    public int getCode() {
        if (error != null) {
            return error.getCode();
        }
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        if (error != null) {
            return error.getUser_message();
        }
        return message;
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
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
