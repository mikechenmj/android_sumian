package com.sumian.sd.buz.advisory.contract

import com.sumian.sd.buz.advisory.bean.Advisory
import com.sumian.sd.buz.advisory.presenter.AdvisoryListPresenter

/**
 *
 *Created by sm
 * on 2018/6/4 14:47
 * desc:
 **/
interface AdvisoryListContract {

    interface View {

        fun setPresenter(presenter: AdvisoryListPresenter) {

        }

        fun onFailure(error: String) {

        }

        fun onBegin() {

        }

        fun onFinish() {

        }

        fun onGetAdvisoriesSuccess(advisories: List<Advisory>)

        fun onGetAdvisoriesFailed(error: String)

        fun onGetNextAdvisoriesSuccess(advisories: List<Advisory>)

        fun onRefreshAdvisoriesSuccess(advisories: List<Advisory>)

    }


}