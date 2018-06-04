package com.sumian.sleepdoctor.onlineReport;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/4 10:25
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class OnlineReport {

    /**
     * id : 2
     * title : 睡眠监测报告
     * conversion_status : 1
     * task_id :
     * report_url : https://sleep-doctor-imm-test.oss-cn-shanghai.aliyuncs.com/doctors/online_report/3/34d5fcfe-6785-43ee-bbe1-b86a3f71b0bf.pdf
     * deleted_at : null
     * created_at : 1528078554
     * updated_at : 1528078554
     */

    private int id;
    private String title;
    private int conversion_status;
    private String task_id;
    private String report_url;
    private Object deleted_at;
    private int created_at;
    private int updated_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getConversion_status() {
        return conversion_status;
    }

    public void setConversion_status(int conversion_status) {
        this.conversion_status = conversion_status;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getReport_url() {
        return report_url;
    }

    public void setReport_url(String report_url) {
        this.report_url = report_url;
    }

    public Object getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(Object deleted_at) {
        this.deleted_at = deleted_at;
    }

    public int getCreated_at() {
        return created_at;
    }

    public void setCreated_at(int created_at) {
        this.created_at = created_at;
    }

    public int getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(int updated_at) {
        this.updated_at = updated_at;
    }

    public long getCreateAtInMillis() {
        return created_at * 1000L;
    }

    @Override
    public String toString() {
        return "OnlineReport{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", conversion_status=" + conversion_status +
                ", task_id='" + task_id + '\'' +
                ", report_url='" + report_url + '\'' +
                ", deleted_at=" + deleted_at +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                '}';
    }
}
