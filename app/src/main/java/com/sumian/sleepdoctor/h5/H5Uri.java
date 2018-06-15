package com.sumian.sleepdoctor.h5;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/31 17:56
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public interface H5Uri {
    // ---------- record ----------
    String SLEEP_RECORD_RECORD_SLEEP = "question";
    String SLEEP_RECORD_WEEKLY_REPORT = "weekly/{date}";
    // ---------- me ----------
    String MY_MEDICAL_RECORD = "mine/medical-record";
    String DOCTOR_SERVICE = "doctor-service/{id}";
    String ONLINE_REPORT = "online-reports?title={title}&report_url={pdfUrl}";
    String FILL_SCALE = "scale-details/{id}";
    String BIND_DOCTOR = "doctor/{id}";
    String ABOUT_US = "about-us";
    String ADVISORY_GUIDE = "advisory-guide";
}
