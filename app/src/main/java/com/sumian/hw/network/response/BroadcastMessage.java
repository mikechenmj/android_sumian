package com.sumian.hw.network.response;

/**
 * Created by jzz
 * on 2017/11/1.
 * <p>
 * desc:系统广播消息,即我的消息中心的消息
 */

public class BroadcastMessage {

    private String id;
    private String data;
    private String from;
    private long timestamp;
    private long till;
    private String cid;

    public String getId() {
        return id;
    }

    public BroadcastMessage setId(String id) {
        this.id = id;
        return this;
    }

    public String getData() {
        return data;
    }

    public BroadcastMessage setData(String data) {
        this.data = data;
        return this;
    }

    public String getFrom() {
        return from;
    }

    public BroadcastMessage setFrom(String from) {
        this.from = from;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public BroadcastMessage setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public long getTill() {
        return till;
    }

    public BroadcastMessage setTill(long till) {
        this.till = till;
        return this;
    }

    public String getCid() {
        return cid;
    }

    public BroadcastMessage setCid(String cid) {
        this.cid = cid;
        return this;
    }

    @Override
    public String toString() {
        return "BroadcastMessage{" +
            "id='" + id + '\'' +
            ", data='" + data + '\'' +
            ", from='" + from + '\'' +
            ", timestamp=" + timestamp +
            ", till=" + till +
            ", cid='" + cid + '\'' +
            '}';
    }
}
