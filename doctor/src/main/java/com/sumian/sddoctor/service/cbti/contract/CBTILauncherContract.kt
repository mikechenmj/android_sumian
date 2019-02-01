package com.sumian.sddoctor.service.cbti.contract

import com.sumian.common.mvp.IPresenter
import com.sumian.common.mvp.IView

/**
 * Created by jzz
 *
 * on 2019/1/10
 *
 * desc:
 */
interface CBTILauncherContract {

    interface View : IView {

        fun onLauncherCBTIIntroduction() {

        }

        fun onLauncherCBTIIntroductionWeb() {

        }
    }

    interface Presenter : IPresenter {
        fun launcherCBTI()
        fun saveLauncherAction()
    }
}