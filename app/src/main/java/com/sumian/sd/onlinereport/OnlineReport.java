package com.sumian.sd.onlinereport;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/4 10:25
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class OnlineReport implements Parcelable {


    /**
     * id : 22
     * title : CBTI初期报告
     * type : 1
     * data : http://sd-dev.sumian.com/scale-details/scales?scale_id=1037,1038,1039&chapter_id=1
     * conversion_status : 1
     * task_id :
     * report_url : http://sd-dev.sumian.com/scale-details/scales?scale_id=1037,1038,1039&chapter_id=1
     * created_at : 1536061737
     * updated_at : 1536061737
     */

    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("type")
    private int type;
    @SerializedName("data")
    private Object data;
    @SerializedName("conversion_status")
    private int conversionStatus;
    @SerializedName("task_id")
    private String taskId;
    @SerializedName("report_url")
    private String reportUrl;
    @SerializedName("created_at")
    private int createdAt;
    @SerializedName("updated_at")
    private int updatedAt;

    protected OnlineReport(Parcel in) {
        id = in.readInt();
        title = in.readString();
        type = in.readInt();
        conversionStatus = in.readInt();
        taskId = in.readString();
        reportUrl = in.readString();
        createdAt = in.readInt();
        updatedAt = in.readInt();
    }

    public static final Creator<OnlineReport> CREATOR = new Creator<OnlineReport>() {
        @Override
        public OnlineReport createFromParcel(Parcel in) {
            return new OnlineReport(in);
        }

        @Override
        public OnlineReport[] newArray(int size) {
            return new OnlineReport[size];
        }
    };

    public long getCreateAtInMillis() {
        return createdAt * 1000L;
    }

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getConversionStatus() {
        return conversionStatus;
    }

    public void setConversionStatus(int conversionStatus) {
        this.conversionStatus = conversionStatus;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getReportUrl() {
        return reportUrl;
    }

    public void setReportUrl(String reportUrl) {
        this.reportUrl = reportUrl;
    }

    public int getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(int createdAt) {
        this.createdAt = createdAt;
    }

    public int getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(int updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeInt(type);
        parcel.writeInt(conversionStatus);
        parcel.writeString(taskId);
        parcel.writeString(reportUrl);
        parcel.writeInt(createdAt);
        parcel.writeInt(updatedAt);
    }
}
