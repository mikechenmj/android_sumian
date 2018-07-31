package com.sumian.app.network.response;

import java.util.List;

/**
 * Created by jzz
 * on 2017/11/2.
 * <p>
 * desc:
 */

public class ResultResponse<T> {

    private List<T> data;
    private Pagination meta;

    public List<T> getData() {
        return data;
    }

    public ResultResponse setData(List<T> data) {
        this.data = data;
        return this;
    }

    public Pagination getMeta() {
        return meta;
    }

    public ResultResponse setMeta(Pagination meta) {
        this.meta = meta;
        return this;
    }

    @Override
    public String toString() {
        return "ResultResponse{" +
            "data=" + data +
            ", meta=" + meta +
            '}';
    }
}
