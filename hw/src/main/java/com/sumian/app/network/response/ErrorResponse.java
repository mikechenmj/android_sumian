package com.sumian.app.network.response;

/**
 * Created by jzz
 * on 2017/9/30
 * <p>
 * desc:
 */

public class ErrorResponse {

    private int status_code;
    private String message;

    public int getStatus_code() {
        return status_code;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
            "status_code=" + status_code +
            ", message='" + message + '\'' +
            '}';
    }
}
