package com.sumian.hw.network.request;

import java.util.List;

/**
 * Created by jzz
 * on 2017/10/19.
 * desc:
 */

public class RawDataBody {

    private String type;//数据类型
    private String cmd_created_at;//传输指令时间
    private String mac;//设备mac地址
    private List<String> data;

    public String getType() {
        return type;
    }

    public RawDataBody setType(String type) {
        this.type = type;
        return this;
    }

    public String getCmd_created_at() {
        return cmd_created_at;
    }

    public RawDataBody setCmd_created_at(String cmd_created_at) {
        this.cmd_created_at = cmd_created_at;
        return this;
    }

    public String getMac() {
        return mac;
    }

    public RawDataBody setMac(String mac) {
        this.mac = mac;
        return this;
    }

    public List<String> getData() {
        return data;
    }

    public RawDataBody setData(List<String> data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return "RawDataBody{" +
            "type='" + type + '\'' +
            ", cmd_created_at='" + cmd_created_at + '\'' +
            ", mac='" + mac + '\'' +
            ", data=" + data +
            '}';
    }
}
