package com.sumian.hw.network.response;

/**
 * Created by jzz
 * on 2017/11/9.
 * <p>
 * desc:
 */

public class ErrorLeancloudResponse {

    private int code;
    private String error;

    public int getCode() {
        return code;
    }

    public ErrorLeancloudResponse setCode(int code) {
        this.code = code;
        return this;
    }

    public String getError() {
        return error;
    }

    public ErrorLeancloudResponse setError(String error) {
        this.error = error;
        return this;
    }

    @Override
    public String toString() {
        return "ErrorLeancloudResponse{" +
            "code=" + code +
            ", error='" + error + '\'' +
            '}';
    }
}
