package com.sumian.common.container.delegate

import android.content.Context
import retrofit2.Call

/**
 * Created by sm
 *
 * on 2018/12/5
 *
 * desc:
 *
 */
interface IContainerDelegate {

    fun setup(context: Context)

    fun onShowLoading()

    fun onDismissLoading()

    fun addCallAction(call: Call<*>)

    fun onCancel()

    fun onRelease()
}