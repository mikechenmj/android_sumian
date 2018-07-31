package com.sumian.app.oss.bean;

/**
 * Created by jzz
 * on 2018/1/2.
 * desc:
 */

public class OssResponse {

    private String access_key_id;// access key id
    private String access_key_secret;//access key secret
    private String security_token;//security token
    private String expiration;//security token 过期时间
    private String bucket;//bucket;
    private String endpoint;//endpoint
    private String callback_url;//OSS 回调地址
    private String callback_body;//回调 body
    private String object;//头像上传的路径

    public String getAccess_key_id() {
        return access_key_id;
    }

    public void setAccess_key_id(String access_key_id) {
        this.access_key_id = access_key_id;
    }

    public String getAccess_key_secret() {
        return access_key_secret;
    }

    public void setAccess_key_secret(String access_key_secret) {
        this.access_key_secret = access_key_secret;
    }

    public String getSecurity_token() {
        return security_token;
    }

    public void setSecurity_token(String security_token) {
        this.security_token = security_token;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getCallback_url() {
        return callback_url;
    }

    public void setCallback_url(String callback_url) {
        this.callback_url = callback_url;
    }

    public String getCallback_body() {
        return callback_body;
    }

    public void setCallback_body(String callback_body) {
        this.callback_body = callback_body;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return "OssResponse{" +
            "access_key_id='" + access_key_id + '\'' +
            ", access_key_secret='" + access_key_secret + '\'' +
            ", security_token='" + security_token + '\'' +
            ", expiration='" + expiration + '\'' +
            ", bucket='" + bucket + '\'' +
            ", endpoint='" + endpoint + '\'' +
            ", callback_url='" + callback_url + '\'' +
            ", callback_body='" + callback_body + '\'' +
            ", object='" + object + '\'' +
            '}';
    }
}
