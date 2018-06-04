package com.sumian.sleepdoctor.network.response;

import java.util.List;

/**
 * Created by jzz
 * on 2018/1/19.
 * desc:
 */
@SuppressWarnings("WeakerAccess")
public class PaginationResponse<Data> {

    public Data data;
    public Pagination meta;

    @Override
    public String toString() {
        return "PaginationResponse{" +
                "data=" + data +
                ", meta=" + meta +
                '}';
    }

    public static class Pagination {

        public int total;
        public int count;
        public int per_page;
        public int current_page;
        public int total_page;
        public List<Links> links;

        @Override
        public String toString() {
            return "Pagination{" +
                    "total=" + total +
                    ", count=" + count +
                    ", per_page=" + per_page +
                    ", current_page=" + current_page +
                    ", total_page=" + total_page +
                    ", links=" + links +
                    '}';
        }
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
