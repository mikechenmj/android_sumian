package com.sumian.sd.network.response;

/**
 * Created by jzz
 * on 2017/11/15.
 * <p>
 * desc:配置信息
 */

public class ConfigInfo {

    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ConfigInfo{" +
            "name='" + name + '\'' +
            ", value='" + value + '\'' +
            '}';
    }
}
