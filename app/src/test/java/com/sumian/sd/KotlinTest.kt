package com.sumian.sd

import com.google.gson.reflect.TypeToken
import com.sumian.sd.account.bean.UserInfo
import com.sumian.sd.account.config.SumianConfig
import com.sumian.sd.h5.bean.H5BaseResponse
import com.sumian.sd.utils.JsonUtil
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
        val regex = String.format(".{%d,%d}", SumianConfig.PASSWORD_LENGTH_MIN, SumianConfig.PASSWORD_LENGTH_MAX)
        System.out.println(regex)
        System.out.println("12313".matches(regex.toRegex()))
    }

}