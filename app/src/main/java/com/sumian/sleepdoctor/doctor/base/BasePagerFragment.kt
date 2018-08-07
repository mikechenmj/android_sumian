package com.sumian.sleepdoctor.doctor.base

import com.sumian.sleepdoctor.base.SdBaseFragment
import com.sumian.sleepdoctor.base.SdBasePresenter
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
abstract class BasePagerFragment<Presenter : SdBasePresenter<Any>> : SdBaseFragment<Presenter>(), OnEnabletabCallback {
}