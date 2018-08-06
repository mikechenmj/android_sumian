package com.sumian.hw.network.response;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/6 11:33
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class ErrorInfo {

    /**
     * code : 1
     * user_message : 账号或密码错误
     * internal_message : 账号或密码错误
     * more_info : null
     */

    private int code;
    private String user_message;
    private String internal_message;
    private Object more_info;

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

    public Object getMore_info() {
        return more_info;
    }

    public void setMore_info(Object more_info) {
        this.more_info = more_info;
    }
}
