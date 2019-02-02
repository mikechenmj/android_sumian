package com.sumian.sddoctor.service.cbti.presenter

import com.sumian.common.base.BaseViewModel
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.service.cbti.bean.MessageBoard
import com.sumian.sddoctor.service.cbti.contract.CBTIMessageBoardDetailContract

class CBTIMessageBoardDetailPresenter private constructor(var view: CBTIMessageBoardDetailContract.View?)
    : BaseViewModel() {
    companion object {
        @JvmStatic
        fun init(view: CBTIMessageBoardDetailContract.View?): CBTIMessageBoardDetailPresenter {
            return CBTIMessageBoardDetailPresenter(view)
        }
    }

     fun getMsgBoardDetail(msgInt: Int) {
        view?.showLoading()
        val call = AppManager.getHttpService().getMsgKeyboardDetail(msgInt)
         addCall(call)
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