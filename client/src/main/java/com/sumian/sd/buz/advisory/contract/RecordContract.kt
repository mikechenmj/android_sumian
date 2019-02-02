package com.sumian.sd.buz.advisory.contract

import com.sumian.sd.base.SdBaseView
import com.sumian.sd.buz.advisory.bean.Advisory
import com.sumian.sd.buz.advisory.presenter.RecordPresenter

/**
 *
 *Created by sm
 * on 2018/6/6 12:03
 * desc:
 **/
interface RecordContract {


    interface View : SdBaseView<RecordPresenter> {

        fun onGetAdvisoryDetailSuccess(advisory: Advisory)

        fun onGetAdvisoryDetailFailed(error: String)

    }

    interface Presenter {

        fun getAdvisoryDetail(advisoryId: Int = 0)
    }


}