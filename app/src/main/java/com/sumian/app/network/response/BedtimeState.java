package com.sumian.app.network.response;

import java.util.List;

/**
 * Created by jzz
 * on 2017/11/27.
 * <p>
 * desc:
 */

public class BedtimeState {
    private List<String> bedtime_state;

    public List<String> getBedtime_state() {
        return bedtime_state;
    }

    public BedtimeState setBedtime_state(List<String> bedtime_state) {
        this.bedtime_state = bedtime_state;
        return this;
    }

    @Override
    public String toString() {
        return "BedtimeState{" +
            "bedtime_state=" + bedtime_state +
            '}';
    }
}
