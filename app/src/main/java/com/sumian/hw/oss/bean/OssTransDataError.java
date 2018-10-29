package com.sumian.hw.oss.bean;

import java.io.Serializable;

public class OssTransDataError implements Serializable {

    private int code;    //code=3 文件名存在
    private String user_message;
    private String internal_message;
    private String more_info;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getUser_message() {
        return user_message;
    }

    public void setUser_message(String user_message) {
        this.user_message = user_message;
    }

    public String getInternal_message() {
        return internal_message;
    }

    public void setInternal_message(String internal_message) {
        this.internal_message = internal_message;
    }

    public String getMore_info() {
        return more_info;
    }

    public void setMore_info(String more_info) {
        this.more_info = more_info;
    }

    @Override
    public String toString() {
        return "OssTransDataError{" +
                "code=" + code +
                ", user_message='" + user_message + '\'' +
                ", internal_message='" + internal_message + '\'' +
                ", more_info='" + more_info + '\'' +
                '}';
    }
}
