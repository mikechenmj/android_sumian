package com.sumian.sd.base

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/10 13:27
 * desc   :
 * version: 1.0
 */
object  Counter {
    private var count =0
    fun count(): Int {
        return count++
    }
}