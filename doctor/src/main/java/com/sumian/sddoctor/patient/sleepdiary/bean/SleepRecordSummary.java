package com.sumian.sddoctor.patient.sleepdiary.bean;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/30 21:12
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class SleepRecordSummary {

    /**
     * id : 277
     * date : 1525449600
     * is_today : false
     * has_doctors_evaluation : true
     */

    private int id;
    @SerializedName("date")
    private int dateInSecond;
    @SerializedName("is_today")
    private boolean isToday;
    @SerializedName("has_doctors_evaluation")
    private boolean hasDoctorsEvaluation;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDateInSecond() {
        return dateInSecond;
    }

    public void setDateInSecond(int dateInSecond) {
        this.dateInSecond = dateInSecond;
    }

    public boolean isIsToday() {
        return isToday;
    }

    public void setIsToday(boolean isToday) {
        this.isToday = isToday;
    }

    public boolean isHasDoctorsEvaluation() {
        return hasDoctorsEvaluation;
    }

    public void setHasDoctorsEvaluation(boolean hasDoctorsEvaluation) {
        this.hasDoctorsEvaluation = hasDoctorsEvaluation;
    }

    public long getDateInMillis() {
        return dateInSecond * 1000L;
    }

    @NonNull
    @Override
    public String toString() {
        return "SleepRecordSummary{" +
                "id=" + id +
                ", dateInSecond=" + dateInSecond +
                ", isToday=" + isToday +
                ", hasDoctorsEvaluation=" + hasDoctorsEvaluation +
                '}';
    }
}
