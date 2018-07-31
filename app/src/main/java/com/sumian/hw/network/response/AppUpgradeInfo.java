package com.sumian.hw.network.response;

import java.io.Serializable;

/**
 * Created by jzz
 * on 2017/12/15.
 * desc:
 */

public class AppUpgradeInfo implements Serializable, Cloneable {

    public String version;
    public String description;
    public boolean need_force_update;

    @Override
    public String toString() {
        return "AppUpgradeInfo{" +
            "version='" + version + '\'' +
            ", description='" + description + '\'' +
            ", need_force_update=" + need_force_update +
            '}';
    }

    @Override
    public AppUpgradeInfo clone() throws CloneNotSupportedException {
        return (AppUpgradeInfo) super.clone();
    }
}
