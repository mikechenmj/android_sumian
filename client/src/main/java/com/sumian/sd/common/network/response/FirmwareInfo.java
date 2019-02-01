package com.sumian.sd.common.network.response;

import com.sumian.sd.buz.upgrade.bean.VersionInfo;

/**
 * Created by jzz
 * on 2017/10/31.
 * <p>
 * desc:
 */

public class FirmwareInfo {

    public VersionInfo monitor;
    public VersionInfo sleeper;

    @Override
    public String toString() {
        return "FirmwareInfo{" +
            "monitor=" + monitor +
            ", sleeper=" + sleeper +
            '}';
    }
}
