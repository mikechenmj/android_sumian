package com.sumian.sd.account.bindMobile

import com.blankj.utilcode.util.ToastUtils
import com.sumian.sd.account.bean.Token
import com.sumian.sd.app.AppManager
import com.sumian.sd.network.callback.BaseResponseCallback
import com.sumian.sd.network.response.ErrorResponse

/**
 * Created by sm
 * on 2018/2/28.
 * desc:
 */
class BindMobilePresenter private constructor(view: BindMobileContract.View) : BindMobileContract.Presenter {

    private var mView: BindMobileContract.View? = view

    init {
        view.setPresenter(this)
        this.mView = view
    }

    companion object {

        @JvmStatic
        fun init(view: BindMobileContract.View) {
            BindMobilePresenter(view)
        }

    }


    override fun bindOpenSocial(mobile: String, captcha: String, socialType: Int, socialInfo: String) {

        mView?.onBegin()

        val map = mutableMapOf<String, Any>()

        map["mobile"] = mobile
        map["captcha"] = captcha
        map["type"] = socialType
        map["info"] = socialInfo

        val call = AppManager
                .getHttpService()
                .bindSocial(map)
        addCall(call)
        call
                .enqueue(object : BaseResponseCallback<Token>() {

                    override fun onSuccess(response: Token?) {
                        AppManager.getAccountViewModel().updateToken(response)
                        mView?.bindOpenSocialSuccess(response)
                    }

                    override fun onFailure(code: Int, message: String) {
                        mView?.onFailure(message)
                        ToastUtils.showShort(message)
                    }

                    override fun onFinish() {
                        super.onFinish()
                        mView?.onFinish()
                    }

                })

    }

    override fun doSendCaptcha(mobile: String) {

        mView?.onBegin()

        val call = AppManager.getHttpService().getCaptcha(mobile)
        addCall(call)
        call.enqueue(object : BaseResponseCallback<Unit>() {
            override fun onFailure(code: Int, message: String) {
                mView?.onFailure(message)
            }

            override fun onSuccess(response: Unit?) {
                mView?.onSendCaptchaSuccess()
            }

            override fun onFinish() {
                super.onFinish()
                mView?.onFinish()
            }

        })

    }


}