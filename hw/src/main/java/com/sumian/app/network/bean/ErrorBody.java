package com.sumian.app.network.bean;

/**
 * Created by jzz
 * on 2017/9/26
 * <p>
 * desc:
 */

public class ErrorBody {

    private String message;
    private int status_code;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus_code() {
        return status_code;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }

    @Override
    public String toString() {
        return "ErrorBody{" +
            "message='" + message + '\'' +
            ", status_code=" + status_code +
            '}';
    }
}
