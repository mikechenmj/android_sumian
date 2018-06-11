package com.sumian.sleepdoctor.improve.advisory.model

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.sumian.sleepdoctor.improve.advisory.bean.Advisory

/**
 *
 *Created by sm
 * on 2018/6/8 11:23
 * desc:
 **/
class AdvisoryViewModel : ViewModel() {

    private val mAdvisoryLiveData: MutableLiveData<ArrayList<Advisory>> by lazy {
        MutableLiveData<ArrayList<Advisory>>()
    }

    fun notifyAdvisory(advisory: Advisory) {

        var tmpCacheAdvisories = mAdvisoryLiveData.value

        if (tmpCacheAdvisories == null) {
            tmpCacheAdvisories = arrayListOf()
        }

        var tmpIndex: Int = -1

        if (tmpCacheAdvisories.isNotEmpty()) {
            for (i: Int in 0 until tmpCacheAdvisories.size) {
                val tmpAdvisory = tmpCacheAdvisories[i]
                if (tmpAdvisory.id == advisory.id) {
                    tmpCacheAdvisories[i] = advisory
                    tmpIndex = i
                    break
                }
            }
        }

        if (tmpIndex == -1) {
            tmpCacheAdvisories.add(advisory)
        }

        mAdvisoryLiveData.postValue(tmpCacheAdvisories)
    }

    fun getAdvisoryLiveData(): LiveData<ArrayList<Advisory>> {
        return mAdvisoryLiveData
    }

    fun getCacheAdvisory(): Advisory {
        return mAdvisoryLiveData.value?.lastOrNull()!!
    }

    fun getCacheAdvisoryFromId(advisoryId: Int): Advisory {
        var advisory: Advisory? = null
        for (it in mAdvisoryLiveData.value!!) {
            if (it.id == advisoryId) {
                advisory = it
                break
            }
        }
        return advisory!!
    }

}