package com.sumian.sd.network.response;

import java.util.List;

/**
 * Created by jzz
 * on 2018/1/19.
 * desc:
 */
@SuppressWarnings("WeakerAccess")
public class PaginationResponse<T> {

    public List<T> data;
    public Pagination meta;

    @Override
    public String toString() {
        return "PaginationResponse{" +
                "data=" + data +
                ", meta=" + meta +
                '}';
    }

    public static class Links {

        public String next;

        @Override
        public String toString() {
            return "Links{" +
                    "next='" + next + '\'' +
                    '}';
        }
    }
}
