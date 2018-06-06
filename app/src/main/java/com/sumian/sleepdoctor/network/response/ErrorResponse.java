package com.sumian.sleepdoctor.network.response;

/**
 * Created by jzz
 * on 2018/1/17.
 * desc:
 */

@SuppressWarnings("WeakerAccess")
public class ErrorResponse {
    public static final int STATUS_CODE_ERROR_UNKNOWN = 0;
    public static final int STATUS_CODE_NOT_FOUND = 404;

    public String message;
    public int status_code;

    public ErrorResponse() {
    }

    public ErrorResponse(int status_code, String message) {
        this.status_code = status_code;
        this.message = message;
    }

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
