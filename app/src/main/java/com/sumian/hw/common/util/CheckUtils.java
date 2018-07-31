package com.sumian.hw.common.util;


import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jzz
 * on 2017/4/25
 * <p>
 * desc:
 */

public class CheckUtils {

    /**
     * 检查邮件地址
     *
     * @param email email
     * @return isEmail
     */
    public static boolean isEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        }
        Pattern p = Pattern.compile("^(.+?)@(.+?)\\.(.+?)$");
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 检测手机号
     *
     * @param phone phone
     * @return true/false
     */
    public static boolean isPhoneNum(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return false;
        }
        Pattern p = Pattern.compile("^[1][34578][0-9]\\d{8}$");
        Matcher m = p.matcher(phone);

        return m.matches();
    }


    /**
     * 检测密码
     *
     * @param password pwd
     * @return true/false
     */
    public static boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) && Pattern.matches("^[a-zA-Z0-9]{6,20}$", password);
    }

    /**
     * 判断输入是否是指令
     *
     * @param command command
     * @return true/false
     */
    public static boolean isCommand(String command) {
        return !TextUtils.isEmpty(command) && Pattern.matches("^[a-zA-Z0-9]+$", command);
    }

}
