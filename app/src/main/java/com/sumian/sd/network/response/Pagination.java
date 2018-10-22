package com.sumian.sd.network.response;

/**
 * Created by jzz
 * on 2017/10/11.
 * desc:
 */

public class Pagination {

    private int total;
    private int count;
    private int per_page;
    private int current_page;
    private int total_pages;


    public int getTotal() {
        return total;
    }

    public Pagination setTotal(int total) {
        this.total = total;
        return this;
    }

    public int getCount() {
        return count;
    }

    public Pagination setCount(int count) {
        this.count = count;
        return this;
    }

    public int getPer_page() {
        return per_page;
    }

    public Pagination setPer_page(int per_page) {
        this.per_page = per_page;
        return this;
    }

    public int getCurrent_page() {
        return current_page;
    }

    public Pagination setCurrent_page(int current_page) {
        this.current_page = current_page;
        return this;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public Pagination setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
        return this;
    }

    @Override
    public String toString() {
        return "Pagination{" +
                "total=" + total +
                ", count=" + count +
                ", per_page=" + per_page +
                ", current_page=" + current_page +
                ", total_pages=" + total_pages +
                '}';
    }

}
