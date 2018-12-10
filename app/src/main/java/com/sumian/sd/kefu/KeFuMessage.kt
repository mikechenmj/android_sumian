package com.sumian.sd.kefu

/**
 * Created by sm
 *
 * on 2018/12/10
 *
 * desc:
 *
 */
data class KeFuMessage(val code: Int, val message: String) {
    fun isRegisterOk(): Boolean = code == 200
}