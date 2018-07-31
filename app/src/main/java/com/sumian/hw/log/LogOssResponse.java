package com.sumian.hw.log;

/**
 * Created by sm
 * on 2018/6/12 14:15
 * desc:
 **/
public class LogOssResponse {

    public String access_key_id;
    public String access_key_secret;
    public String security_token;
    public String expiration;
    public String bucket;
    public String endpoint;
    public String callback_url;
    public String object;
    public int size;//文件大小

    @Override
    public String toString() {
        return "LogOssResponse{" +
            "access_key_id='" + access_key_id + '\'' +
            ", access_key_secret='" + access_key_secret + '\'' +
            ", security_token='" + security_token + '\'' +
            ", expiration='" + expiration + '\'' +
            ", bucket='" + bucket + '\'' +
            ", endpoint='" + endpoint + '\'' +
            ", callback_url='" + callback_url + '\'' +
            ", object='" + object + '\'' +
            ", size=" + size +
            '}';
    }
}
