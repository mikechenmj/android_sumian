package com.sumian.sd.service.cbti.presenter

import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.app.AppManager
import com.sumian.sd.base.SdBasePresenter.mCalls
import com.sumian.sd.network.callback.BaseSdResponseCallback
import com.sumian.sd.service.cbti.contract.CBTIMessageBoardActionContract

class CBTIMessageBoardActionPresenter private constructor(view: CBTIMessageBoardActionContract.View) : CBTIMessageBoardActionContract.Presenter {

    companion object {
        @JvmStatic
        fun init(view: CBTIMessageBoardActionContract.View): CBTIMessageBoardActionContract.Presenter = CBTIMessageBoardActionPresenter(view)
    }

    private var mView: CBTIMessageBoardActionContract.View? = null

    init {
        view.setPresenter(this)
        mView = view
    }

    override fun publishMessage(message: String, type: Int, isAnonymous: Int) {
        mView?.onBegin()
        val map = mutableMapOf<String, Any>()
        map["type"] = type
        map["message"] = message
        map["anonymous"] = isAnonymous
        val call = AppManager.getSdHttpService().writeCBTIMessageBoard(map = map)
        mCalls.add(call)
        call.enqueue(object : BaseSdResponseCallback<Any>() {
            override fun onSuccess(response: Any?) {
                mView?.onPublishMessageBoardSuccess("留言成功")
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.onPublishMessageBoardFailed(error = errorResponse.message)
            }

            override fun onFinish() {
                super.onFinish()
                mView?.onFinish()
            }
        })
    }
}