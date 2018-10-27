package com.sumian.hw.upgrade.bean;

import com.sumian.hw.utils.NumberUtil;

import java.io.Serializable;

/**
 * Created by jzz
 * on 2017/11/23.
 * <p>
 * desc: 版本信息
 */
public class VersionInfo implements Serializable, Cloneable {

    private int versionCode;
    private String version;
    private String url;
    private String md5;

    public int getVersionCode() {
        return versionCode = NumberUtil.formatVersionCode(version);
    }

    public VersionInfo setVersionCode(int versionCode) {
        this.versionCode = versionCode;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public VersionInfo setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public VersionInfo setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getMd5() {
        return md5;
    }

    public VersionInfo setMd5(String md5) {
        this.md5 = md5;
        return this;
    }

    @Override
    public String toString() {
        return "VersionInfo{" +
                "versionCode=" + versionCode +
                ", version='" + version + '\'' +
                ", url='" + url + '\'' +
                ", md5='" + md5 + '\'' +
                '}';
    }

    @Override
    public VersionInfo clone() throws CloneNotSupportedException {
        return (VersionInfo) super.clone();//这里是深拷贝,不是浅拷贝  因为变量都是常量类型的 string
    }
}
