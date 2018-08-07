package com.sumian.sleepdoctor.homepage

import com.sumian.sleepdoctor.base.SdBasePresenter
import com.sumian.sleepdoctor.base.SdBaseView

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