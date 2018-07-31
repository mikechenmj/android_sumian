package com.sumian.hw.network.request;

/**
 * Created by jzz
 * on 2017/10/10.
 * desc:
 */

public class SleepPackageBody {

    private String items;

    public String getItems() {
        return items;
    }

    public SleepPackageBody setItems(String items) {
        this.items = items;
        return this;
    }

    @Override
    public String toString() {
        return "SleepPackageBody{" +
            "items='" + items + '\'' +
            '}';
    }
}
