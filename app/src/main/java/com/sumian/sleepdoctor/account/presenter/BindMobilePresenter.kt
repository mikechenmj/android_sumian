package com.sumian.sleepdoctor.account.presenter

import com.sumian.sleepdoctor.account.bean.Token
import com.sumian.sleepdoctor.account.contract.BindMobileContract
import com.sumian.sleepdoctor.app.AppManager
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback

/**
 * Created by sm
 * on 2018/2/28.
 * desc:
 */
class BindMobilePresenter private constructor(view: BindMobileContract.View) : BindMobileContract.Presenter {

    private var mView: BindMobileContract.View? = view

    init {
        view.bindPresenter(this)
        this.mView = view
    }

    companion object {

        fun init(view: BindMobileContract.View) {
            BindMobilePresenter(view)
        }

    }


    override fun bindOpenSocial(mobile: String, captcha: String, socialType: Int, socialInfo: String) {

        mView!!.onBegin()

        AppManager
                .getHttpService()
                .bindSocial(mapOf("mobile" to mobile, "captcha" to captcha, "type" to 0, "info" to socialInfo))
                .enqueue(object : BaseResponseCallback<Token>() {

                    override fun onSuccess(response: Token?) {
                        AppManager.getAccountViewModel().updateToken(response)
                        mView!!.bindOpenSocialSuccess(response)
                    }

                    override fun onFailure(error: String?) {
                        mView!!.onFailure(error)
                    }

                    override fun onFinish() {
                        super.onFinish()
                        mView!!.onFinish()
                    }

                })

    }

    override fun doSendCaptcha(mobile: String) {

        mView!!.onBegin()

        AppManager.getHttpService().getCaptcha(mobile).enqueue(object : BaseResponseCallback<Unit>() {

            override fun onSuccess(response: Unit?) {
                mView!!.onSendCaptchaSuccess()
            }

            override fun onFailure(error: String?) {
                mView!!.onFailure(error)
            }

            override fun onFinish() {
                super.onFinish()
                mView!!.onFinish()
            }

        })

    }


}