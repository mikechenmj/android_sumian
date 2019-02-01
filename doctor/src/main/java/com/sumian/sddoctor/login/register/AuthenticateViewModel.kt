package com.sumian.sddoctor.login.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/28 19:21
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class AuthenticateViewModel : ViewModel() {

    val mProgressLiveData: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    fun goNextStep() {
        mProgressLiveData.value = mProgressLiveData.value ?: 0 + 1
    }

}