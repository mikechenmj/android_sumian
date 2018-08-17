package com.sumian.sd.account.config;

/**
 * Created by jzz
 * on 2017/9/28
 * <p>
 * desc:app 相关配置文件
 */

public final class SumianConfig {

    public static final String LOGIN_CAPTCHA_TYPE = "login_captcha_type";
    public static final String BIND_SOCIAL_CAPTCHA_TYPE = "bind_social_captcha_type";
    private static final String SETTING_NAME = "sumian_doctor_setting";
    public static final int PASSWORD_LENGTH_MIN = 6;
    public static final int PASSWORD_LENGTH_MAX = 16;
    public static final int REAL_NAME_LENGTH_MIN = 2;
    public static final int REAL_NAME_LENGTH_MAX = 16;
    public static final int NICK_NAME_LENGTH_MIN = 1;
    public static final int NICK_NAME_LENGTH_MAX = 16;
    public static final int SEND_CAPTCHA_COLD_TIME = 30;
    public static final int CAPTCHA_LENGTH = 6;

}
