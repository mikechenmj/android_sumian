package com.sumian.sd.buz.homepage

import com.sumian.sd.base.SdBasePresenter
import com.sumian.sd.base.SdBaseView

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/7/10 15:23
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class HomepageContract {
    interface View : SdBaseView<Presenter> {

    }

    interface Presenter : SdBasePresenter<View> {

    }
}