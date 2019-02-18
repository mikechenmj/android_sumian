package com.sumian.sd.buz.devicemanager;

import android.bluetooth.BluetoothAdapter;

import com.sumian.sd.R;
import com.sumian.sd.app.App;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

import androidx.annotation.NonNull;

/**
 * Created by sm
 * on 2018/3/24.
 * <p>
 * desc:
 */

@SuppressWarnings({"unused", "WeakerAccess"})
public class BlueDevice implements Serializable, Comparable<BlueDevice> {
    public static final int STATUS_UNCONNECTED = 0;
    public static final int STATUS_CONNECTING = 1;
    public static final int STATUS_CONNECTED = 2;

    // sleeper pa status
    public static final int PA_STATUS_NOT_PA = 0;
    public static final int PA_STATUS_TURNING_ON_PA = 1;
    public static final int PA_STATUS_PA = 2;
    // cmd
    public static final int MONITORING_CMD_CLOSE = 0x00;
    public static final int MONITORING_CMD_OPEN = 0x01;

    public static final int CHANNEL_TYPE_UNKNOWN = -1;
    public static final int CHANNEL_TYPE_NORMAL = 0;
    public static final int CHANNEL_TYPE_CLINIC = 1;


    // monitor status
    public String name;
    public String mac;
    public String sn;
    public String version;
    public String bomVersion;
    public transient int channelType = CHANNEL_TYPE_UNKNOWN; //临床版，正式版
    /**
     * @see #STATUS_UNCONNECTED
     * @see #STATUS_CONNECTING
     * @see #STATUS_CONNECTED
     */
    public transient int status;
    public transient int battery;//电池电量
    public transient int rssi;//信号强度
    public transient boolean isMonitoring;
    public transient boolean isSyncing;

    // sleeper status
    public String sleeperName;
    public String sleeperMac;
    public String sleeperSn;
    public String sleeperVersion;
    public String sleeperBomVersion;
    public transient int sleeperStatus;
    public transient int sleeperBattery;
    public transient int sleeperPaStatus;

    @NotNull
    @Override
    public String toString() {
        return "BlueDevice{" +
                "name='" + name + '\'' +
                ", mac='" + mac + '\'' +
                ", sn='" + sn + '\'' +
                ", version='" + version + '\'' +
                ", status=" + status +
                ", battery=" + battery +
                ", rssi=" + rssi +
                ", isMonitoring=" + isMonitoring +
                ", isSyncing=" + isSyncing +
                ", sleeperName='" + sleeperName + '\'' +
                ", sleeperMac='" + sleeperMac + '\'' +
                ", sleeperSn='" + sleeperSn + '\'' +
                ", sleeperVersion='" + sleeperVersion + '\'' +
                ", sleeperStatus=" + sleeperStatus +
                ", sleeperBattery=" + sleeperBattery +
                ", sleeperPaStatus=" + sleeperPaStatus +
                '}';
    }

    public boolean isAvailableBlueDevice() {
        return BluetoothAdapter.checkBluetoothAddress(mac);
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
        return status == STATUS_CONNECTED;
    }

    public boolean isSleeperConnected() {
        return sleeperStatus == STATUS_CONNECTED;
    }

    public boolean isSleeperPa() {
        return isSleeperConnected() && sleeperPaStatus == PA_STATUS_PA;
    }

    public int getSleeperStatus() {
        return sleeperStatus;
    }

    public int getSleeperBattery() {
        return sleeperBattery;
    }

    public void resetSleeper() {
        sleeperName = App.Companion.getAppContext().getString(R.string.speed_sleeper);
        sleeperStatus = STATUS_UNCONNECTED;
        sleeperBattery = 0;
        sleeperPaStatus = PA_STATUS_NOT_PA;
    }
}
