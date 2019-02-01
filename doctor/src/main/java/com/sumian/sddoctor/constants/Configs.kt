package com.sumian.sddoctor.constants

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/14 21:16
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class Configs {
    companion object {
        // account info
        const val PASSWORD_LENGTH_MIN = 6
        const val PASSWORD_LENGTH_MAX = 16
        const val CAPTCHA_LENGTH = 6

        // doctor info
        const val DOCTOR_NAME_MIN_LENGTH = 2
        const val DOCTOR_NAME_MAX_LENGTH = 16
        const val DOCTOR_NICKNAME_MIN_LENGTH = 1
        const val DOCTOR_NICKNAME_MAX_LENGTH = 16
        const val HOSPITAL_NAME_MIN_LENGTH = 4
        const val HOSPITAL_NAME_MAX_LENGTH = 20
        const val DEPARTMENT_NAME_MIN_LENGTH = 2
        const val DEPARTMENT_NAME_MAX_LENGTH = 10
        const val JOB_TITLE_NAME_MIN_LENGTH = 2
        const val JOB_TITLE_NAME_MAX_LENGTH = 10
    }
}