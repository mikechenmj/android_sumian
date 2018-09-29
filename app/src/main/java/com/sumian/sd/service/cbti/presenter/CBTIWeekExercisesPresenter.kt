package com.sumian.sd.service.cbti.presenter

import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.app.AppManager
import com.sumian.sd.base.SdBasePresenter.mCalls
import com.sumian.sd.network.callback.BaseSdResponseCallback
import com.sumian.sd.service.cbti.bean.CBTIDataResponse
import com.sumian.sd.service.cbti.bean.Exercise
import com.sumian.sd.service.cbti.contract.CBTIWeekExercisesContract

/**
 * Created by dq
 *
 * on 2018/7/12
 *
 * desc:
 */
class CBTIWeekExercisesPresenter constructor(view: CBTIWeekExercisesContract.View) : CBTIWeekExercisesContract.Presenter {

    private val mView: CBTIWeekExercisesContract.View

    init {
        view.setPresenter(this)
        this.mView = view
    }

    companion object {

        fun init(view: CBTIWeekExercisesContract.View): CBTIWeekExercisesContract.Presenter {
            return CBTIWeekExercisesPresenter(view)
        }
    }

    override fun getCBTIWeekExercises(id: Int) {

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

        mCalls.add(call)
    }

}