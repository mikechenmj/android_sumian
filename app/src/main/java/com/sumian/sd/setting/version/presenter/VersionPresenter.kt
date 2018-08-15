package com.sumian.sd.setting.version.presenter

import com.sumian.sd.setting.version.bean.Version
import com.sumian.sd.app.App
import com.sumian.sd.app.AppManager
import com.sumian.sd.network.callback.BaseResponseCallback
import com.sumian.sd.network.response.ErrorResponse
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

        AppManager.getHttpService().getAppVersion(currentVersion = currentVersion).enqueue(object : BaseResponseCallback<Version>() {

            override fun onSuccess(response: Version?) {
                response?.let {
                    mView?.onGetVersionSuccess(response)
                    it.version?.let {

                        val currentVersions = currentVersion.split(".")

                        var isHaveUpgrade = false

                        it.split(".").forEachIndexed { index, versionCode
                            ->
                            run {
                                if (versionCode > currentVersions[index]) {
                                    isHaveUpgrade = true
                                    return@forEachIndexed
                                }
                            }
                        }

                        mView?.onHaveUpgrade(isHaveUpgrade, response.need_force_update)
                    }
                }
            }

            override fun onFailure(code: Int, message: String) {
                mView?.onGetVersionFailed(error = message)
            }

            override fun onFinish() {
                super.onFinish()
                mView?.onFinish()
            }

        })
    }
}