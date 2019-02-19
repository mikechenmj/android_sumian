package com.sumian.sddoctor.account.kefu

/**
 * Created by sm
 *
 * on 2018/12/10
 *
 * desc:
 *
 */
data class KeFuMessage(val code: Int, val message: String) {
    fun isRegisterOk(): Boolean = (code == REGISTER_SUCCESS)

    companion object {
        const val REGISTER_SUCCESS = 200   //注册成功
        const val REPEAT_ERROR = 1  //重复注册
        const val OTHER_ERROR = 2  //其他错误,需重新注册
    }
}