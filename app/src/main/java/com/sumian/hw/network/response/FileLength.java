package com.sumian.hw.network.response;

/**
 * Created by jzz
 * on 2017/10/23.
 * desc:
 */

public class FileLength {

    private String mac;//设备mac
    private String type;//原始数据类型   emg  pulse  speed
    private String cmd_created_at;//文件创建时间
    private long file_length;//文件长度
    private String url;//文件保存地址

    public String getMac() {
        return mac;
    }

    public FileLength setMac(String mac) {
        this.mac = mac;
        return this;
    }

    public String getType() {
        return type;
    }

    public FileLength setType(String type) {
        this.type = type;
        return this;
    }

    public String getCmd_created_at() {
        return cmd_created_at;
    }

    public FileLength setCmd_created_at(String cmd_created_at) {
        this.cmd_created_at = cmd_created_at;
        return this;
    }

    public long getFile_length() {
        return file_length;
    }

    public FileLength setFile_length(long file_length) {
        this.file_length = file_length;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public FileLength setUrl(String url) {
        this.url = url;
        return this;
    }

    @Override
    public String toString() {
        return "FileLength{" +
            "mac='" + mac + '\'' +
            ", type='" + type + '\'' +
            ", cmd_created_at='" + cmd_created_at + '\'' +
            ", file_length=" + file_length +
            ", url='" + url + '\'' +
            '}';
    }
}
