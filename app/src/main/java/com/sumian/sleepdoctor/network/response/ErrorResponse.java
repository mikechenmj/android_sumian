package com.sumian.sleepdoctor.network.response;

/**
 * Created by jzz
 * on 2018/1/17.
 * desc:
 */

public class ErrorResponse {
    private static final int STATUS_CODE_NOT_FOUND = 404;

    public String message;
    public int status_code;

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "message='" + message + '\'' +
                ", status_code=" + status_code +
                '}';
    }

    public boolean isNotFound() {
        return status_code == STATUS_CODE_NOT_FOUND;
    }
}
