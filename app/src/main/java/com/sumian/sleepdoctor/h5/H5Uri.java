package com.sumian.sleepdoctor.h5;

/**
 * <pre>
 *     @author : Zhan Xuzhao
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
    String MY_MEDICAL_RECORD = "mine/medical";
    String DOCTOR_SERVICE = "doctor-service/{id}";
    String ONLINE_REPORT = "online-reports?title={title}&report_url={pdfUrl}";
    String FILL_SCALE = "scale-details/{id}";
    String BIND_DOCTOR = "doctor/{id}";
    String ABOUT_US = "about-us";
    String ADVISORY_GUIDE = "advisory-guide";
    String USER_AGREEMENT_URL = "user-agreement";
    String USER_POLICY_URL = "privacy-policy";
    String MY_TARGET = "my-target?from=";// from=mine 表示从我的目标进入，from=newUser 表示新用户登录时进入
    String MY_TARGET_FROM_MINE = MY_TARGET + "mine";
    String MY_TARGET_FROM_NEW_USER = MY_TARGET + "newUser";
    String CBTI_INTRODUCTION = "cbti";
    String SLEEP_PRESCRIPTION = "prescription?data={data}";
    String CBTI_EXERCISES = "cbti/exercises?id={course-id}";
    String CBTI_WEEK_REVIEW = "/cbti/week-review?review={last_chapter_summary}";
}
