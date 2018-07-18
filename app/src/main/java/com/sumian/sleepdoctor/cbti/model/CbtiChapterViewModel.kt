package com.sumian.sleepdoctor.cbti.model

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.sumian.sleepdoctor.cbti.bean.Courses
import com.sumian.sleepdoctor.cbti.bean.Exercise
import com.sumian.sleepdoctor.cbti.bean.Lesson

/**
 * Created by dq
 *
 * on 2018/7/13
 *
 * desc:  CBTI viewModel
 */

class CbtiChapterViewModel : ViewModel() {

    private val mCBTICoursesLiveData: MutableLiveData<Courses> by lazy {
        MutableLiveData<Courses>()
    }

    private val mCBTILessonLiveData: MutableLiveData<Lesson> by lazy {
        MutableLiveData<Lesson>()
    }

    private val mCBTIExercisesLiveData: MutableLiveData<Exercise> by lazy {
        MutableLiveData<Exercise>()
    }

    fun notifyCBTICourses(courses: Courses) {
        mCBTICoursesLiveData.postValue(courses)
    }

    fun getCBTICourses(): Courses? {
        return this.mCBTICoursesLiveData.value
    }

    fun notifyCBTICouresProgress(progress: Int) {
        val courses = this.mCBTICoursesLiveData.value
        courses?.meta?.chapter_progress = progress
        mCBTICoursesLiveData.postValue(courses)
    }

    fun getCBTICoursesLiveData(): LiveData<Courses> {
        return mCBTICoursesLiveData
    }


}