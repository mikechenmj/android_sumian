package com.sumian.sd.common.h5;

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
    String FILL_SCALE = "scale-details/{scale_distribution_id}";
    String BIND_DOCTOR = "doctor/null?url={url}";//  doctor/{id}
    String ABOUT_US = "about-us";
    String ADVISORY_GUIDE = "advisory-guide";
    String USER_AGREEMENT_URL = "user-agreement?theme=white";
    String USER_POLICY_URL = "privacy-policy";
    String MY_TARGET = "my-target?from=";// from=mine 表示从我的目标进入，from=newUser 表示新用户登录时进入
    String MY_TARGET_FROM_MINE = MY_TARGET + "mine";
    String MY_TARGET_FROM_NEW_USER = MY_TARGET + "newUser";
    String CBTI = "cbti";//CBTI 详情页（如果未购买过）
    String CBTI_INTRODUCTION = "cbtiIntroduce";//CBTI 了解更多
    String CBTI_OPEN_SCALES = "openCbtiScales";
    String SLEEP_PRESCRIPTION = "prescription";
    String CBTI_EXERCISES = "cbti/exercises?id={course-id}";
    String CBTI_WEEK_REVIEW = "cbti/week-review?review={last_chapter_summary}";
    String NEW_USER_GUIDE = "guide?from=newUser";
    String CBTI_RELAXATIONS = "cbti/relaxations";
    String CBTI_RELAXATIONS_SHARE = "cbti/relaxations/{id}?isShare=true";
    String CBTI_SLEEP_HEALTH = "cbti/sleep-health";
    String NATIVE_ROUTE = "native-route?data={pageData}&token={token}";
    String SLEEPER_TALK_SHARE = "sumian-friends-detail?id={id}&isShare=true";
    String SLEEPER_TALK_PAGE = "sumianFriendsDetail";

}
