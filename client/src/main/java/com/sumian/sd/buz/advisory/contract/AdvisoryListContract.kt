package com.sumian.sd.buz.advisory.contract

import com.sumian.sd.base.SdBaseView
import com.sumian.sd.buz.advisory.bean.Advisory
import com.sumian.sd.buz.advisory.presenter.AdvisoryListPresenter

/**
 *
 *Created by sm
 * on 2018/6/4 14:47
 * desc:
 **/
interface AdvisoryListContract {

    interface View : SdBaseView<AdvisoryListPresenter> {

        fun onGetAdvisoriesSuccess(advisories: List<Advisory>)

        fun onGetAdvisoriesFailed(error: String)

        fun onGetNextAdvisoriesSuccess(advisories: List<Advisory>)

        fun onRefreshAdvisoriesSuccess(advisories: List<Advisory>)

    }


}