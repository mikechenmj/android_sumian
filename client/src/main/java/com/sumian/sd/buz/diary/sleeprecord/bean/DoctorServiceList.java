package com.sumian.sd.buz.diary.sleeprecord.bean;

import com.google.gson.annotations.SerializedName;
import com.sumian.sd.buz.doctor.bean.DoctorService;

import java.util.List;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/4 19:51
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class DoctorServiceList {

    @SerializedName("data")
    private List<DoctorService> serviceList;

    public List<DoctorService> getServiceList() {
        return serviceList;
    }

    public void setServiceList(List<DoctorService> serviceList) {
        this.serviceList = serviceList;
    }
}
