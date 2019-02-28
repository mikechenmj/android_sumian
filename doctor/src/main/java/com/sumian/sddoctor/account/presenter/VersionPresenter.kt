@file:Suppress("NestedLambdaShadowedImplicitParameter")

package com.sumian.sddoctor.account.presenter

import com.sumian.common.base.BaseViewModel
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.VersionUtil
import com.sumian.sddoctor.account.bean.Version
import com.sumian.sddoctor.account.contract.VersionContract
import com.sumian.sddoctor.app.App
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.util.UiUtils

/**
 * <pre>
 *     @author : sm

 *     e-mail : yaoqi.y@sumian.com
 *     time: 2018/6/29 14:48
 *
 *     version: 1.0
 *
 *     desc:
 *
 * </pre>
 */
class VersionPresenter private constructor(view: VersionContract.View) : BaseViewModel() {

    private var mView: VersionContract.View? = null

    init {
        this.mView = view
    }

    companion object {
        fun init(view: VersionContract.View): VersionPresenter {
            return VersionPresenter(view)
        }
    }

    fun getVersion() {

        this.mView?.showLoading()

        var currentVersion = UiUtils.getPackageInfo(App.getAppContext()).versionName

        if (currentVersion.indexOf("-") != -1) {
            currentVersion = currentVersion.subSequence(0, currentVersion.indexOf("-")).toString()
        }

        AppManager.getHttpService().getAppVersion(currentVersion = currentVersion).enqueue(object : BaseSdResponseCallback<Version>() {

            override fun onSuccess(response: Version?) {
                response?.let {
                    mView?.onGetVersionSuccess(it)
                    var isHaveUpgrade: Boolean
                    it.version?.let {
                        val onlineVersionCodes = it.split(".")
                        val currentVersionCodes = currentVersion.split(".")
                        isHaveUpgrade = VersionUtil.hasNewVersion(onlineVersionCodes, currentVersionCodes)
                        mView?.onHaveUpgrade(isHaveUpgrade, response.need_force_update, response.showShowDialog(), response.description)
                    }
                }
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.onGetVersionFailed(error = errorResponse.message)
            }

            override fun onFinish() {
                super.onFinish()
                mView?.dismissLoading()
            }

        })
    }
}