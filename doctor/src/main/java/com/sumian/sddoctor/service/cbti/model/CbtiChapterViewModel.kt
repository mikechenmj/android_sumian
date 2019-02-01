package com.sumian.sddoctor.service.cbti.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sumian.sddoctor.service.cbti.bean.CBTIMeta
import com.sumian.sddoctor.service.cbti.bean.Course
import com.sumian.sddoctor.service.cbti.bean.Exercise

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

    fun getCBTICourses(): List<Course>? {
        return this.mCBTICoursesLiveData.value
    }

    fun getCBTICoursesLiveData(): LiveData<List<Course>> {
        return mCBTICoursesLiveData
    }

    fun getCBTICourseMetaLiveData(): LiveData<CBTIMeta> {
        return mCBTICourseMetaLiveData
    }

    fun notifyCourses(courses: List<Course>) {
        mCBTICoursesLiveData.postValue(courses)
    }

    fun notifyCBTICourseMeta(cbtiMeta: CBTIMeta) {
        mCBTICourseMetaLiveData.postValue(cbtiMeta)
    }
}