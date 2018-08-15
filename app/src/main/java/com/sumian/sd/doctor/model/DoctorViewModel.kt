package com.sumian.sd.doctor.model

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.sumian.sd.doctor.bean.Doctor

/**
 *
 *Created by sm
 * on 2018/6/8 13:57
 * desc:
 **/
class DoctorViewModel : ViewModel() {

    private val mDoctorLiveData: MutableLiveData<Doctor> by lazy { MutableLiveData<Doctor>() }

    fun notifyDoctor(doctor: Doctor?) {
        this.mDoctorLiveData.postValue(doctor)
    }

    fun getCacheDoctor(): Doctor? {
        return this.mDoctorLiveData.value
    }

    fun getDoctorLiveData(): LiveData<Doctor> {
        return this.mDoctorLiveData
    }
}