package com.sumian.sleepdoctor.cbti.presenter

import com.sumian.sleepdoctor.app.AppManager
import com.sumian.sleepdoctor.base.SdBasePresenter.mCalls
import com.sumian.sleepdoctor.cbti.bean.CBTIDataResponse
import com.sumian.sleepdoctor.cbti.bean.Exercise
import com.sumian.sleepdoctor.cbti.contract.CBTIWeekExercisesContract
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback

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

        val call = AppManager.getHttpService().getCBTIExerciseWeekPart(id)
        call.enqueue(object : BaseResponseCallback<CBTIDataResponse<Exercise>>() {

            override fun onSuccess(response: CBTIDataResponse<Exercise>?) {
                response?.let {
                    mView.onGetCBTIWeekPracticeSuccess(response.data)
                }
            }

            override fun onFailure(code: Int, message: String) {
                mView.onGetCBTIWeekPracticeFailed(message)
            }

            override fun onFinish() {
                super.onFinish()
                mView.onFinish()
            }
        })

        mCalls.add(call)
    }

}