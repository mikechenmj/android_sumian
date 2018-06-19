package com.sumian.sleepdoctor.account.bindMobile

import com.blankj.utilcode.util.ToastUtils
import com.sumian.sleepdoctor.account.bean.Token
import com.sumian.sleepdoctor.app.AppManager
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback
import com.sumian.sleepdoctor.network.response.ErrorResponse

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

        mView!!.onBegin()

        val map = mutableMapOf<String, Any>()

        map["mobile"] = mobile
        map["captcha"] = captcha
        map["type"] = socialType
        map["info"] = socialInfo

        AppManager
                .getHttpService()
                .bindSocial(map)
                .enqueue(object : BaseResponseCallback<Token>() {

                    override fun onSuccess(response: Token?) {
                        AppManager.getAccountViewModel().updateToken(response)
                        mView!!.bindOpenSocialSuccess(response)
                    }

                    override fun onFailure(errorResponse: ErrorResponse?) {
                        mView!!.onFailure(errorResponse?.message)
                        ToastUtils.showShort(errorResponse?.message)
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
            override fun onFailure(errorResponse: ErrorResponse?) {
                mView!!.onFailure(errorResponse?.message)
            }

            override fun onSuccess(response: Unit?) {
                mView!!.onSendCaptchaSuccess()
            }

            override fun onFinish() {
                super.onFinish()
                mView!!.onFinish()
            }

        })

    }


}