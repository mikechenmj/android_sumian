package com.sumian.sleepdoctor.widget.webview;

/**
 * Created by sm
 * on 2018/5/29 19:33
 * desc:
 **/
public class SBridgeResult<T> {

    public int code; //response code
    public String message;//response message
    public T result;//result;

    @Override
    public String toString() {
        return "SBridgeResult{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", result=" + result +
                '}';
    }
}
