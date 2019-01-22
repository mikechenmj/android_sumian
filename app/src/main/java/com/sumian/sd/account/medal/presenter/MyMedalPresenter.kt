package com.sumian.sd.account.medal.presenter

import com.sumian.common.mvp.IPresenter.Companion.mCalls
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.network.response.PaginationResponseV2
import com.sumian.sd.account.medal.bean.Medal
import com.sumian.sd.account.medal.contract.MyMedalContract
import com.sumian.sd.app.AppManager
import com.sumian.sd.network.callback.BaseSdResponseCallback

/**
 * Created by jzz
 *
 * on 2019/1/22
 *
 * desc:
 */
class MyMedalPresenter private constructor(private val view: MyMedalContract.View?) : MyMedalContract.Presenter {


    companion object {
        @JvmStatic
        fun create(view: MyMedalContract.View): MyMedalContract.Presenter = MyMedalPresenter(view)
    }

    override fun getMyMetal() {
        view?.showLoading()

        val call = AppManager.getSdHttpService().getMyMetalList()
        mCalls.add(call)
        call.enqueue(object : BaseSdResponseCallback<PaginationResponseV2<Medal>>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                view?.onGetMyMedalListFailed(error = errorResponse.message)
            }

            override fun onSuccess(response: PaginationResponseV2<Medal>?) {
                response?.let {
                    val data = response.data
                    view?.onGetMyMedalListSuccess(data)
                }
            }

            override fun onFinish() {
                super.onFinish()
                view?.dismissLoading()
            }
        })
    }
}