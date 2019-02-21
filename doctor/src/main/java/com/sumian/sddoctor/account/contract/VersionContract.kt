package com.sumian.sddoctor.account.contract

import com.sumian.sddoctor.account.bean.Version
import com.sumian.sddoctor.base.BasePresenter
import com.sumian.sddoctor.base.BaseView

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

    interface View : BaseView {

        fun onGetVersionSuccess(version: Version)

        fun onGetVersionFailed(error: String)

        fun onHaveUpgrade(isHaveUpgrade: Boolean, isHaveForce: Boolean, isShowDialog: Boolean, versionMsg: String?)

    }


    interface Presenter : BasePresenter {

        fun getVersion()

    }
}