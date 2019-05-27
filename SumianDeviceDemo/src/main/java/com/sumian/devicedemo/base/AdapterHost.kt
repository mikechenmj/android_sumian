package com.sumian.devicedemo.base

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/6 14:28
 * desc   :
 * version: 1.0
 */
interface AdapterHost<T> {
    fun onItemClick(data: T)
}