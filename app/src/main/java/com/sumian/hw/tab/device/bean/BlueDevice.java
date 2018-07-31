package com.sumian.hw.tab.device.bean;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Locale;

/**
 * Created by jzz
 * on 2017/8/16.
 * desc:
 */

public class BlueDevice implements Serializable, Comparable<BlueDevice> {

    private String name;//型号编号
    private String mac;//mac
    private int state;//正在连接状态  0x00 idle 0x01  正在连接  0x02连接成功
    private boolean isCache;//是否是缓存
    private int rssi;//信号强度

    public String getName() {
        return name;
    }

    public BlueDevice setName(String name) {
        this.name = name;
        return this;
    }

    public String getMac() {
        return mac;
    }

    public BlueDevice setMac(String mac) {
        this.mac = mac;
        return this;
    }

    public int getState() {
        return state;
    }

    public BlueDevice setState(int state) {
        this.state = state;
        return this;
    }

    public boolean isCache() {
        return isCache;
    }

    public BlueDevice setCache(boolean cache) {
        isCache = cache;
        return this;
    }

    public int getRssi() {
        return rssi;
    }

    public BlueDevice setRssi(int rssi) {
        this.rssi = rssi;
        return this;
    }

    public String getDfuMac() {
        //CD:9D:C4:08:D8:9D
        String mac = this.mac;

        String[] split = mac.split(":");

        StringBuilder macSb = new StringBuilder();
        for (String s : split) {
            macSb.append(s);
        }

        //由于 dfu 升级需要设备 mac+1

        //uint64 x old mac;, y new mac;
        // y = (( x & 0xFF ) + 1) + ((x >> 8) << 8);
        long oldMac = Long.parseLong(macSb.toString(), 16);
        long newMac = ((oldMac & 0xff) + 1) + ((oldMac >> 8) << 8);


        macSb.delete(0, macSb.length());

        String hexString = Long.toHexString(newMac);

        for (int i = 0, len = hexString.length(); i < len; i++) {
            if (i % 2 == 0) {
                macSb.append(hexString.substring(i, i + 2));
                if (i != len - 2) {
                    macSb.append(":");
                }
            }
        }

        return macSb.toString().toUpperCase(Locale.getDefault());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlueDevice that = (BlueDevice) o;

        //if (state != that.state) return false;
        //if (isCache != that.isCache) return false;
        //if (rssi != that.rssi) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return mac != null ? mac.equals(that.mac) : that.mac == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (mac != null ? mac.hashCode() : 0);
        //result = 31 * result + state;
        //result = 31 * result + (isCache ? 1 : 0);
        //  result = 31 * result + rssi;
        return result;
    }

    @Override
    public String toString() {
        return "BlueDevice{" +
            "name='" + name + '\'' +
            ", mac='" + mac + '\'' +
            ", state=" + state +
            ", isCache=" + isCache +
            ", rssi=" + rssi +
            '}';
    }

    @Override
    public int compareTo(@NonNull BlueDevice o) {
        return o.rssi - this.rssi;
    }
}
