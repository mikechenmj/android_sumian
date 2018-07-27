package com.sumian.sleepdoctor.homepage

import com.sumian.sleepdoctor.base.BasePresenter
import com.sumian.sleepdoctor.base.BaseView

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
    interface View : BaseView<Presenter> {

    }

    interface Presenter : BasePresenter<View> {

    }
}