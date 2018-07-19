package com.sumian.sleepdoctor.setting.version.contract

import com.sumian.sleepdoctor.setting.version.bean.Version
import com.sumian.sleepdoctor.base.BasePresenter
import com.sumian.sleepdoctor.base.BaseView

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

    interface View : BaseView<Presenter> {

        fun onGetVersionSuccess(version: Version)

        fun onGetVersionFailed(error: String)

        fun onHaveUpgrade(isHaveUpgrade: Boolean, isHaveForce: Boolean)

    }


    interface Presenter : BasePresenter<Any> {

        fun getVersion()

    }
}