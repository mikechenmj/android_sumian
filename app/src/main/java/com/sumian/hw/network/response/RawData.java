package com.sumian.hw.network.response;

/**
 * Created by jzz
 * on 2017/10/23.
 * desc:  临床实验原始数据返回bean
 */

public class RawData {

    private long file_length;//文件长度
    private String url;//文件下载地址

    public long getFile_length() {
        return file_length;
    }

    public RawData setFile_length(long file_length) {
        this.file_length = file_length;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public RawData setUrl(String url) {
        this.url = url;
        return this;
    }

    @Override
    public String toString() {
        return "RawData{" +
            "file_length=" + file_length +
            ", url='" + url + '\'' +
            '}';
    }
}
