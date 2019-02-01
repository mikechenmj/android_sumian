package com.sumian.sddoctor.constants

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/21 18:04
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class H5Uri {
    companion object {
        const val NATIVE_ROUTE = "native-route?data={pageData}&token={token}"

        const val USER_PROTOCOL = "user-protocol"
        const val DOCTOR_SHARE = "doctor-code/{id}"
        const val PATIENT_FILE = "medical/{id}"
        const val ONLINE_REPORT = "online-reports?title={title}&report_url={pdfUrl}"
        const val EVALUATION_ID = "evaluation/{id}" //睡眠日记周评估
        const val ADVISORY_GUIDE = "advisory-guide"
        const val CBTI_INTRODUCTION = "cbtiIntroduction"//CBTI 了解更多
        const val CBTI = "cbti"//CBTI 详情页（如果未购买过）
        const val CBTI_OPEN_SCALES = "openCbtiScales"
        const val CBTI_EXERCISES = "cbti/exercises?id={course-id}"
        const val CBTI_WEEK_REVIEW = "cbti/week-review?review={last_chapter_summary}"
        const val CBTI_RELAXATIONS = "cbti/relaxations"
        const val CBTI_RELAXATIONS_SHARE = "cbti/relaxations/{id}?isShare=true"
        const val CBTI_SLEEP_HEALTH = "cbti/sleep-health"
    }
}