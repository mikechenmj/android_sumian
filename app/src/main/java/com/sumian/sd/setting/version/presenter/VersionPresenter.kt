@file:Suppress("REDUNDANT_LABEL_WARNING", "NOT_A_FUNCTION_LABEL_WARNING")

package com.sumian.sd.setting.version.presenter

import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.VersionUtil
import com.sumian.sd.app.App
import com.sumian.sd.app.AppManager
import com.sumian.sd.base.SdBasePresenter.mCalls
import com.sumian.sd.network.callback.BaseSdResponseCallback
import com.sumian.sd.setting.version.bean.Version
import com.sumian.sd.setting.version.contract.VersionContract
import com.sumian.sd.utils.UiUtils

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
class VersionPresenter private constructor(view: VersionContract.View) : VersionContract.Presenter {

    private var mView: VersionContract.View? = null

    init {
        this.mView = view
    }

    companion object {
        fun init(view: VersionContract.View): VersionContract.Presenter {
            return VersionPresenter(view)
        }
    }

    override fun getVersion() {

        this.mView?.onBegin()

        var currentVersion = UiUtils.getPackageInfo(App.getAppContext()).versionName

        if (currentVersion.indexOf("-") != -1) {
            currentVersion = currentVersion.subSequence(0, currentVersion.indexOf("-")).toString()
        }

        val call = AppManager.getSdHttpService().getAppVersion(currentVersion = currentVersion)
        mCalls.add(call)

        call.enqueue(object : BaseSdResponseCallback<Version>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.onGetVersionFailed(error = errorResponse.message)
            }

            override fun onSuccess(response: Version?) {
                response?.let { it ->
                    mView?.onGetVersionSuccess(response)

                    it.version?.let {

                        val onlineVersionCodes = it.split(".")
                        val currentVersionCodes = currentVersion.split(".")
                        val isHaveUpgrade = VersionUtil.hasNewVersion(onlineVersionCodes, currentVersionCodes)

                        mView?.onHaveUpgrade(isHaveUpgrade, response.need_force_update, response.description)
                    }
                }
            }

            override fun onFinish() {
                super.onFinish()
                mView?.onFinish()
            }

        })
    }
}