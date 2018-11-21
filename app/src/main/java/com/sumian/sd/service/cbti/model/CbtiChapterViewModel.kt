package com.sumian.sd.service.cbti.model

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.sumian.sd.service.cbti.bean.CBTIMeta
import com.sumian.sd.service.cbti.bean.Course
import com.sumian.sd.service.cbti.bean.Exercise

/**
 * Created by dq
 *
 * on 2018/7/13
 *
 * desc:  CBTI viewModel
 */

class CbtiChapterViewModel : ViewModel() {

    private val mCBTICoursesLiveData: MutableLiveData<List<Course>> by lazy {
        MutableLiveData<List<Course>>()
    }

    private val mCBTICourseMetaLiveData: MutableLiveData<CBTIMeta> by lazy {
        MutableLiveData<CBTIMeta>()
    }

    private val mCBTIExercisesLiveData: MutableLiveData<List<Exercise>> by lazy {
        MutableLiveData<List<Exercise>>()
    }

    private val mIsShowKeyboardLiveData: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun getCBTICourses(): List<Course>? {
        return this.mCBTICoursesLiveData.value
    }

    fun getCBTICoursesLiveData(): LiveData<List<Course>> {
        return mCBTICoursesLiveData
    }

    fun getCBTICourseMetaLiveData(): LiveData<CBTIMeta> {
        return mCBTICourseMetaLiveData
    }

    fun getKeyboardLiveData(): LiveData<Boolean> = mIsShowKeyboardLiveData

    fun notifyCourses(courses: List<Course>) {
        mCBTICoursesLiveData.postValue(courses)
    }

    fun notifyCBTICourseMeta(cbtiMeta: CBTIMeta) {
        mCBTICourseMetaLiveData.postValue(cbtiMeta)
    }

    fun notifyKeyBoard(isShowKeyBoard: Boolean) {
        if (mIsShowKeyboardLiveData.value == isShowKeyBoard) return
        mIsShowKeyboardLiveData.postValue(isShowKeyBoard)
    }

}