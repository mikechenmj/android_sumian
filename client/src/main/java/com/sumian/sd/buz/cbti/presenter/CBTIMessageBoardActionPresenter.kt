package com.sumian.sd.buz.cbti.presenter

import com.sumian.common.base.BaseViewModel
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.cbti.activity.CBTIWeekCoursePartActivity
import com.sumian.sd.common.network.callback.BaseSdResponseCallback

class CBTIMessageBoardActionPresenter private constructor(view: CBTIWeekCoursePartActivity) : BaseViewModel() {

    companion object {
        @JvmStatic
        fun init(view: CBTIWeekCoursePartActivity): CBTIMessageBoardActionPresenter = CBTIMessageBoardActionPresenter(view)
    }

    private var mView: CBTIWeekCoursePartActivity? = null

    init {
        view.setPresenter(this)
        mView = view
    }

    fun publishMessage(message: String, type: Int, isAnonymous: Int) {
        mView?.onBegin()
        val map = mutableMapOf<String, Any>()
        map["type"] = type
        map["message"] = message
        map["anonymous"] = isAnonymous
        val call = AppManager.getSdHttpService().writeCBTIMessageBoard(map = map)
        addCall(call)
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