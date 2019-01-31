package com.sumian.sd.common.oss.hwbean;

/**
 * desc:透传类型,不是睡眠特征数据的返回结果
 */
public class OssTransData {

    private int id;
    private int user_id;
    private String type;
    private String filename;
    private String url;
    private String app_receive_started_at;
    private String app_receive_ended_at;
    private String monitor_sn;
    private String sleeper_sn;
    private String updated_at;
    private String created_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getApp_receive_started_at() {
        return app_receive_started_at;
    }

    public void setApp_receive_started_at(String app_receive_started_at) {
        this.app_receive_started_at = app_receive_started_at;
    }

    public String getApp_receive_ended_at() {
        return app_receive_ended_at;
    }

    public void setApp_receive_ended_at(String app_receive_ended_at) {
        this.app_receive_ended_at = app_receive_ended_at;
    }

    public String getMonitor_sn() {
        return monitor_sn;
    }

    public void setMonitor_sn(String monitor_sn) {
        this.monitor_sn = monitor_sn;
    }

    public String getSleeper_sn() {
        return sleeper_sn;
    }

    public void setSleeper_sn(String sleeper_sn) {
        this.sleeper_sn = sleeper_sn;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return "OssTransData{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", type='" + type + '\'' +
                ", filename='" + filename + '\'' +
                ", url='" + url + '\'' +
                ", app_receive_started_at='" + app_receive_started_at + '\'' +
                ", app_receive_ended_at='" + app_receive_ended_at + '\'' +
                ", monitor_sn='" + monitor_sn + '\'' +
                ", sleeper_sn='" + sleeper_sn + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", created_at='" + created_at + '\'' +
                '}';
    }
}
