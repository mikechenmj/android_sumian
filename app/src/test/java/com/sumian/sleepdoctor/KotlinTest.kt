package com.sumian.sleepdoctor

import com.google.gson.reflect.TypeToken
import com.sumian.sleepdoctor.account.bean.UserProfile
import com.sumian.sleepdoctor.h5.bean.H5BaseResponse
import com.sumian.sleepdoctor.utils.JsonUtil
import org.junit.Test

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/20 14:18
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class KotlinTest {
    @Test
    fun test() {
//        val u: UserProfile? = UserProfile()
        val u: UserProfile? = null
        val isMale = u?.isMale
        System.out.println(isMale)
        System.out.println(isMale == false)
        System.out.println(isMale == true)

        val data = "{\"code\":0,\"message\":\"保存成功\",\n" +
                "\"result1\":\"{\\\"id\\\":2102,\\\"mobile\\\":\\\"13570464488\\\",\\\"nickname\\\":\\\"詹徐照1\\\",\\\"name\\\":\\\"？？？？？？\\\",\\\"avatar\\\":\\\"https:\\/\\/sleep-doctor.oss-cn-shenzhen.aliyuncs.com\\/avatar\\/103\\/1475807f-672e-4a71-bd46-988f8580321a.png\\\",\\\"area\\\":\\\"未设置\\\",\\\"gender\\\":\\\"female\\\",\\\"birthday\\\":\\\"1990-06\\\",\\\"age\\\":28,\\\"height\\\":\\\"170.0\\\",\\\"weight\\\":\\\"52.0\\\",\\\"bmi\\\":\\\"18.0\\\",\\\"leancloud_id\\\":\\\"sumian-production-8aa1d5fa-af7a-4122-956a-7114bee94655\\\",\\\"doctor_id\\\":1,\\\"bound_at\\\":1527645935,\\\"last_login_at\\\":\\\"2018-06-07 19:22:26\\\",\\\"device_info\\\":\\\"{\\\\\\\"app_version\\\\\\\":\\\\\\\"1.3.0-dev\\\\\\\",\\\\\\\"model\\\\\\\":\\\\\\\"vivo vivo X21A\\\\\\\",\\\\\\\"monitor_fw\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"monitor_sn\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"sleeper_fw\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"sleeper_sn\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"system\\\\\\\":\\\\\\\"Android 8.1.0\\\\\\\"}\\\",\\\"monitor_sn\\\":\\\"A88888888888\\\",\\\"sleeper_sn\\\":\\\"C99999999999\\\",\\\"career\\\":\\\"\\\",\\\"education\\\":\\\"本科或以上\\\",\\\"created_at\\\":1528098077,\\\"updated_at\\\":1531810601,\\\"im_id\\\":\\\"develop8232e119d8f59aa83050a741631803a6\\\",\\\"im_password\\\":\\\"83d5b11837b0921f7ee745d5b5545cc1\\\",\\\"socialites\\\":[{\\\"id\\\":192,\\\"type\\\":0,\\\"user_id\\\":2102,\\\"open_id\\\":\\\"ouV7E1SWfPLo3pzHtflGSXpS3Xl4\\\",\\\"union_id\\\":\\\"oQT0F0rlH7sbHKmpfWFNg-0xYB34\\\",\\\"nickname\\\":\\\"詹徐照\\\",\\\"app\\\":1,\\\"created_at\\\":1528872109,\\\"updated_at\\\":1528872109}],\\\"doctor\\\":{\\\"id\\\":1,\\\"hospital\\\":\\\"速眠医院55\\\",\\\"department\\\":\\\"呼吸内科\\\",\\\"title\\\":\\\"副主任医师\\\",\\\"qr_code_raw\\\":\\\"https:\\/\\/sleep-doctor-dev.oss-cn-shenzhen.aliyuncs.com\\/doctors\\/qr_code\\/doctor_qr_1_1530231595.png\\\",\\\"introduction\\\":\\\"速眠睡眠中心主任医生。主要临床专业特长： 各种精神与心理障碍诊疗，尤其是难治性焦虑、抑郁和睡眠障碍的临床诊断和药物治疗。\\\",\\\"name\\\":\\\"速眠ys2个气势恢宏豪兔\\\",\\\"avatar\\\":\\\"https:\\/\\/sleep-doctor.oss-cn-shenzhen.aliyuncs.com\\/doctors\\/avatar\\/1\\/93cb9585-fcf3-4888-aed8-678d3a873232.jpg\\\",\\\"introduction_no_tag\\\":\\\"速眠睡眠中心主任医生。主要临床专业特长： 各种精神与心理障碍诊疗，尤其是难治性焦虑、抑郁和睡眠障碍的临床诊断和药物治疗。\\\"}}\",\n" +
                "\"result\":{\"id\":2102,\"mobile\":\"13570464488\",\"nickname\":\"詹徐照1\",\"name\":\"？？？？？？\",\"avatar\":\"https://sleep-doctor.oss-cn-shenzhen.aliyuncs.com/avatar/103/1475807f-672e-4a71-bd46-988f8580321a.png\",\"area\":\"未设置\",\"gender\":\"female\",\"birthday\":\"1990-06\",\"age\":28,\"height\":\"170.0\",\"weight\":\"52.0\",\"bmi\":\"18.0\",\"leancloud_id\":\"sumian-production-8aa1d5fa-af7a-4122-956a-7114bee94655\",\"doctor_id\":1,\"bound_at\":1527645935,\"last_login_at\":\"2018-06-07 19:22:26\",\"device_info\":\"{\\\"app_version\\\":\\\"1.3.0-dev\\\",\\\"model\\\":\\\"vivo vivo X21A\\\",\\\"monitor_fw\\\":\\\"\\\",\\\"monitor_sn\\\":\\\"\\\",\\\"sleeper_fw\\\":\\\"\\\",\\\"sleeper_sn\\\":\\\"\\\",\\\"system\\\":\\\"Android 8.1.0\\\"}\",\"monitor_sn\":\"A88888888888\",\"sleeper_sn\":\"C99999999999\",\"career\":\"\",\"education\":\"本科或以上\",\"created_at\":1528098077,\"updated_at\":1531810601,\"im_id\":\"develop8232e119d8f59aa83050a741631803a6\",\"im_password\":\"83d5b11837b0921f7ee745d5b5545cc1\",\"socialites\":[{\"id\":192,\"type\":0,\"user_id\":2102,\"open_id\":\"ouV7E1SWfPLo3pzHtflGSXpS3Xl4\",\"union_id\":\"oQT0F0rlH7sbHKmpfWFNg-0xYB34\",\"nickname\":\"詹徐照\",\"app\":1,\"created_at\":1528872109,\"updated_at\":1528872109}],\"doctor\":{\"id\":1,\"hospital\":\"速眠医院55\",\"department\":\"呼吸内科\",\"title\":\"副主任医师\",\"qr_code_raw\":\"https://sleep-doctor-dev.oss-cn-shenzhen.aliyuncs.com/doctors/qr_code/doctor_qr_1_1530231595.png\",\"introduction\":\"速眠睡眠中心主任医生。主要临床专业特长： 各种精神与心理障碍诊疗，尤其是难治性焦虑、抑郁和睡眠障碍的临床诊断和药物治疗。\",\"name\":\"速眠ys2个气势恢宏豪兔\",\"avatar\":\"https://sleep-doctor.oss-cn-shenzhen.aliyuncs.com/doctors/avatar/1/93cb9585-fcf3-4888-aed8-678d3a873232.jpg\",\"introduction_no_tag\":\"速眠睡眠中心主任医生。主要临床专业特长： 各种精神与心理障碍诊疗，尤其是难治性焦虑、抑郁和睡眠障碍的临床诊断和药物治疗。\"}}\n" +
                "}"
        val typeToken = object : TypeToken<H5BaseResponse<UserProfile>>() {}
        val response: H5BaseResponse<UserProfile>? = JsonUtil.fromJson(data, typeToken.type)
        System.out.println(response)
    }

    @Test
    fun testHex() {

        val binaryHex = "11111111111111111111111000000000000000000000000000000000000000000000000000000000000000000000000000000000000000011111111111110000000111111110000000000001111111111111"

        System.currentTimeMillis() / 1000L
        println("hexStr=${binaryHex.toBigInteger(2).toString(16)}")

    }
}