package com.sumian.sd.setting.version.contract

import com.sumian.sd.setting.version.bean.Version
import com.sumian.sd.base.SdBasePresenter
import com.sumian.sd.base.SdBaseView

/**
 * <pre>
 *     @author : sm

 *     e-mail : yaoqi.y@sumian.com
 *     time: 2018/6/29 14:46
 *
 *     version: 1.0
 *
 *     desc:
 *
 * </pre>
 */
interface VersionContract {

    interface View : SdBaseView<Presenter> {

        fun onGetVersionSuccess(version: Version)

        fun onGetVersionFailed(error: String)

        fun onHaveUpgrade(isHaveUpgrade: Boolean, isHaveForce: Boolean)

    }


    interface Presenter : SdBasePresenter<Any> {

        fun getVersion()

    }
}