package com.sumian.sleepdoctor.doctor.base

import com.sumian.sleepdoctor.base.BaseFragment
import com.sumian.sleepdoctor.base.BasePresenter
import com.sumian.sleepdoctor.doctor.callback.OnEnabletabCallback

/**
 * <pre>
 *     @author : sm
 *     @e-mail : yaoqi.y@sumian.com
 *     @time   : 2018/6/15 17:57
 *
 *     @version: 1.0
 *
 *     @desc   :
 *
 * </pre>
 */
abstract class BasePagerFragment<Presenter : BasePresenter<Any>> : BaseFragment<Presenter>(), OnEnabletabCallback {
}