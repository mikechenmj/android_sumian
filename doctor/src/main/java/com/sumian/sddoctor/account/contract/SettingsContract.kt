package com.sumian.sddoctor.account.contract

import com.sumian.sddoctor.base.BaseActivity
import com.sumian.sddoctor.base.BasePresenter
import com.sumian.sddoctor.base.BaseView

/**
 * <pre>
 *     @author : sm
 *     @e-mail : yaoqi.y@sumian.com
 *     @time   : 2018/6/25 16:52
 *
 *     @version: 1.0
 *
 *     @desc   :
 *
 * </pre>
 */
interface SettingsContract {

    interface View : BaseView {

        fun onUnbindSuccess()

        fun onUnBindFailed(error: String)

        fun onBindSuccess()

        fun onBindFailed(error: String)

        fun onCancelBind(error: String)
    }


    interface Presenter : BasePresenter {

        fun unbindWechat()

        fun bindWechat(activity: BaseActivity)
    }
}