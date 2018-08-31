package com.sumian.common.mvp

import retrofit2.Call


/**
 * Created by sm
 *
 * on 2018/8/1
 *
 * desc:
 *
 */
interface IPresenter {

    companion object {

        val mCalls: MutableList<Call<*>> by lazy {
            mutableListOf<Call<*>>()
        }

    }

    fun onRelease() {
        mCalls.forEach {
            if (it.isExecuted && !it.isCanceled) {
                it.cancel()
            }
        }
    }
}