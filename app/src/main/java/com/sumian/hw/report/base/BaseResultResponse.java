package com.sumian.hw.report.base;

import java.util.List;

/**
 * Created by jzz
 * on 2018/3/12.
 * desc:
 */

public class BaseResultResponse<Data, Meta> {

    public List<Data> data;
    public Meta meta;

    @Override
    public String toString() {
        return "BaseResultResponse{" +
            "data=" + data +
            ", meta=" + meta +
            '}';
    }
}
