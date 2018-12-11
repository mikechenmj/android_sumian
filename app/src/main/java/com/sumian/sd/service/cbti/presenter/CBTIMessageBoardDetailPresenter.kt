package com.sumian.sd.service.cbti.presenter

import com.sumian.common.mvp.IPresenter.Companion.mCalls
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.app.AppManager
import com.sumian.sd.network.callback.BaseSdResponseCallback
import com.sumian.sd.service.cbti.bean.MessageBoard
import com.sumian.sd.service.cbti.contract.CBTIMessageBoardDetailContract

class CBTIMessageBoardDetailPresenter private constructor(var view: CBTIMessageBoardDetailContract.View?)
    : CBTIMessageBoardDetailContract.Presenter {
    companion object {
        @JvmStatic
        fun init(view: CBTIMessageBoardDetailContract.View?): CBTIMessageBoardDetailContract.Presenter {
            return CBTIMessageBoardDetailPresenter(view)
        }
    }

    override fun getMsgBoardDetail(msgInt: Int) {
        view?.showLoading()
        val call = AppManager.getSdHttpService().getMsgKeyboardDetail(msgInt)
        mCalls.add(call)
        call.enqueue(object : BaseSdResponseCallback<MessageBoard>() {
            override fun onSuccess(response: MessageBoard?) {
                if (response == null) {
                    view?.onShowErrorView()
                } else {
                    view?.onGetMsgBoardDetailSuccess(response)
                }
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                view?.onGetMsgBoardDetailFailed(error = errorResponse.message)
                if (errorResponse.code == 1) {
                    view?.onShowErrorView()
                } else {
                    view?.onHideErrorView()
                }
            }

            override fun onFinish() {
                super.onFinish()
                view?.dismissLoading()
            }
        })
    }
}