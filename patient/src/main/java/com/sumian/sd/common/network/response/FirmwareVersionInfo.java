package com.sumian.sd.common.network.response;

import com.sumian.sd.buz.upgrade.bean.VersionInfo;

import androidx.annotation.Nullable;

/**
 * Created by jzz
 * on 2017/10/31.
 * <p>
 * desc:
 */

public class FirmwareVersionInfo {

    public @Nullable VersionInfo monitor;
    public @Nullable VersionInfo sleeper;

    @Override
    public String toString() {
        return "FirmwareVersionInfo{" +
            "monitor=" + monitor +
            ", sleeper=" + sleeper +
            '}';
    }
}
