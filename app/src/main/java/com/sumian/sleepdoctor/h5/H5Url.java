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
public interface H5Url {
    String H5_URI_SLEEP_RECORD_RECORD_SLEEP = "question";
    String H5_URI_DOCTOR_SERVICE = "doctor-service/{id}";
    String H5_URI_ONLINE_REPORT = "online-reports?title={title}&report_url={pdfUrl}";
    String H5_URI_FILL_SCALE = "scale-details/{id}";
    String H5_URI_BIND_DOCTOR = "doctor/{id}";
}
