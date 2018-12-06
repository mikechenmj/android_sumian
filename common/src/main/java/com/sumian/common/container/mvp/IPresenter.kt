package com.sumian.common.container.mvp

import retrofit2.Call

/**
 * Created by sm
 *
 * on 2018/12/5
 *
 * desc:
 *
 */
interface IPresenter {

    fun addCall(call: Call<*>)

    fun onRelease()
}