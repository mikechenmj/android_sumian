package com.sumian.sd.account.medal.presenter

import com.sumian.common.mvp.IPresenter.Companion.mCalls
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.account.medal.bean.MyMedalShare
import com.sumian.sd.account.medal.contract.MyMedalShareContract
import com.sumian.sd.app.AppManager
import com.sumian.sd.network.callback.BaseSdResponseCallback

/**
 * Created by jzz
 *
 * on 2019/1/22
 *
 * desc:
 */
class MyMedalSharePresenter private constructor(var view: MyMedalShareContract.View?) : MyMedalShareContract.Presenter {

    companion object {
        @JvmStatic
        fun create(view: MyMedalShareContract.View?): MyMedalShareContract.Presenter = MyMedalSharePresenter(view)
    }

    override fun getMyMedal(id: Int) {
        view?.showLoading()
        val call = AppManager.getSdHttpService().getMyMetalShareDetail(id)
        mCalls.add(call)
        call.enqueue(object : BaseSdResponseCallback<MyMedalShare>() {
            override fun onSuccess(response: MyMedalShare?) {
                view?.onGetMyMedalSuccess(response!!)
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                view?.onGetMyMedalFailed(error = errorResponse.message)
            }

            override fun onFinish() {
                super.onFinish()
                view?.dismissLoading()
            }
        })
    }

    override fun share(shareType: Int, medalShare: MyMedalShare) {

    }
}