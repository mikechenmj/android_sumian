package com.sumian.sd.service.advisory.contract

import com.sumian.sd.service.advisory.bean.Advisory
import com.sumian.sd.base.SdBasePresenter
import com.sumian.sd.base.SdBaseView

/**
 *
 *Created by sm
 * on 2018/6/6 12:03
 * desc:
 **/
interface RecordContract {


    interface View : SdBaseView<Presenter> {

        fun onGetAdvisoryDetailSuccess(advisory: Advisory)

        fun onGetAdvisoryDetailFailed(error: String)

    }

    interface Presenter : SdBasePresenter<Any> {

        fun getAdvisoryDetail(advisoryId: Int = 0)
    }


}