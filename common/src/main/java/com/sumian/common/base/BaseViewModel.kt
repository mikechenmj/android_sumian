package com.sumian.common.base

import androidx.lifecycle.ViewModel
import retrofit2.Call

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/2/1 15:14
 * desc   :
 * version: 1.0
 */
open class BaseViewModel : ViewModel() {
    private val mCalls = HashSet<Call<*>>()

    fun addCall(call: Call<*>) {
        mCalls.add(call)
    }

    public override fun onCleared() {
        super.onCleared()
        for (call in mCalls) {
            call.cancel()
        }
    }
}