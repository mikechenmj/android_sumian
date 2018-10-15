package com.sumian.hw.device.bean;

import android.bluetooth.BluetoothAdapter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.Locale;

/**
 * Created by sm
 * on 2018/3/24.
 * <p>
 * desc:
 */

@SuppressWarnings({"unused", "WeakerAccess"})
public class BlueDevice implements Serializable, Comparable<BlueDevice> {

    public String name;
    public String mac;
    public String sn;
    public String version;
    public transient int status;////0x00  未连接  0x01  连接中  0x02  在线  0x03 同步数据状态 0x04 pa 模式
    public transient int battery;//电池电量
    public transient int rssi;//信号强度
    public transient @Nullable
    BlueDevice speedSleeper;//监测仪下属的速眠仪;
    public transient boolean isMonitoring;
    public transient boolean isSyncing;
    public transient boolean isPa;

    public static final int STATUS_UNCONNECTED = 0;
    public static final int STATUS_CONNECTING = 1;
    public static final int STATUS_CONNECTED = 2;
    @Deprecated
    public static final int STATUS_SYNCHRONIZING = 3;
    @Deprecated
    public static final int STATUS_PA = 4;
    // cmd
    public static final int MONITORING_CMD_CLOSE = 0x00;
    public static final int MONITORING_CMD_OPEN = 0x01;

    @Override
    public String toString() {
        return "BlueDevice{" +
                "name='" + name + '\'' +
                ", mac='" + mac + '\'' +
                ", sn='" + sn + '\'' +
                ", status=" + status +
                ", battery=" + battery +
                ", rssi=" + rssi +
                ", speedSleeper=" + speedSleeper +
                '}';
    }

    public boolean isAvailableBlueDevice() {
        return BluetoothAdapter.checkBluetoothAddress(mac);
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BlueDevice that = (BlueDevice) o;
        return (name != null ? name.equals(that.name) : that.name == null) && (mac != null ? mac.equals(that.mac) : that.mac == null);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (mac != null ? mac.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(@NonNull BlueDevice o) {
        return o.rssi - this.rssi;
    }

    public boolean isConnected() {
        return status != STATUS_UNCONNECTED && status != STATUS_CONNECTING;
    }

    public boolean isSleeperConnected() {
        return speedSleeper != null && speedSleeper.status == STATUS_CONNECTED;
    }

    public boolean isSleeperPa() {
        return speedSleeper != null && speedSleeper.isPa;
    }

    public int getSleeperStatus() {
        return speedSleeper == null ? BlueDevice.STATUS_UNCONNECTED : speedSleeper.status;
    }

    public int getSleeperBattery() {
        return speedSleeper == null ? 0 : speedSleeper.battery;
    }
}
