package com.sumian.hw.network.request;

/**
 * Created by jzz
 * on 2017/10/24.
 * desc:
 */

public class UploadFileBody {

    private String mac;
    private String type;
    private String cmd_created_at;

    public String getMac() {
        return mac;
    }

    public UploadFileBody setMac(String mac) {
        this.mac = mac;
        return this;
    }

    public String getType() {
        return type;
    }

    public UploadFileBody setType(String type) {
        this.type = type;
        return this;
    }

    public String getCmd_created_at() {
        return cmd_created_at;
    }

    public UploadFileBody setCmd_created_at(String cmd_created_at) {
        this.cmd_created_at = cmd_created_at;
        return this;
    }

    @Override
    public String toString() {
        return "UploadFileBody{" +
            "mac='" + mac + '\'' +
            ", type='" + type + '\'' +
            ", cmd_created_at='" + cmd_created_at + '\'' +
            '}';
    }
}
