package com.sumian.sd.buz.version.contract

import com.sumian.common.base.BaseViewModel
import com.sumian.sd.buz.version.bean.Version

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

    interface View {
        fun setPresenter(presenter: BaseViewModel) {
        }

        fun onFailure(error: String) {
        }

        fun onBegin() {
        }

        fun onFinish() {
        }

        fun onGetVersionSuccess(version: Version)
        fun onGetVersionFailed(error: String)
        fun onHaveUpgrade(isHaveUpgrade: Boolean, isHaveForce: Boolean, isShowDialog: Boolean, versionMsg: String?)
    }


    interface Presenter {
        fun getVersion()
    }
}