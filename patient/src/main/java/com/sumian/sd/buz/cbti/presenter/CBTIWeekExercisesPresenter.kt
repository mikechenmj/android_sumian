package com.sumian.sd.buz.cbti.presenter

import com.sumian.common.base.BaseViewModel
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.cbti.bean.CBTIDataResponse
import com.sumian.sd.buz.cbti.bean.Exercise
import com.sumian.sd.buz.cbti.fragment.ExerciseFragment
import com.sumian.sd.common.network.callback.BaseSdResponseCallback

/**
 * Created by dq
 *
 * on 2018/7/12
 *
 * desc:
 */
class CBTIWeekExercisesPresenter constructor(view: ExerciseFragment) : BaseViewModel() {

    private val mView: ExerciseFragment

    init {
        view.setPresenter(this)
        this.mView = view
    }

    companion object {

        fun init(view: ExerciseFragment): CBTIWeekExercisesPresenter {
            return CBTIWeekExercisesPresenter(view)
        }
    }

    fun getCBTIWeekExercises(id: Int) {

        mView.onBegin()

        val call = AppManager.getSdHttpService().getCBTIExerciseWeekPart(id)
        call.enqueue(object : BaseSdResponseCallback<CBTIDataResponse<Exercise>>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                mView.onGetCBTIWeekPracticeFailed(error = errorResponse.message)
            }

            override fun onSuccess(response: CBTIDataResponse<Exercise>?) {
                response?.let {
                    mView.onGetCBTIWeekPracticeSuccess(response.data)
                }
            }

            override fun onFinish() {
                super.onFinish()
                mView.onFinish()
            }
        })

        addCall(call)
    }

}