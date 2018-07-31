package com.sumian.app.network.request;

import java.util.ArrayList;

/**
 * Created by jzz
 * on 2017/10/7
 * <p>
 * desc:  睡眠数据上报 body
 */

public class SleepyDataBody {

    private String mac;
    private ArrayList<String> packages;

    public String getMac() {
        return mac;
    }

    public SleepyDataBody setMac(String mac) {
        this.mac = mac;
        return this;
    }

    public ArrayList<String> getPackages() {
        return packages;
    }

    public SleepyDataBody setPackages(ArrayList<String> packages) {
        this.packages = packages;
        return this;
    }

    @Override
    public String toString() {
        return "SleepyDataBody{" +
            "mac='" + mac + '\'' +
            ", packages=" + packages +
            '}';
    }
}
