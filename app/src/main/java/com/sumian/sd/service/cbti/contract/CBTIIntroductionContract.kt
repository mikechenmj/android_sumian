package com.sumian.sd.service.cbti.contract

import com.sumian.common.mvp.BaseShowLoadingView
import com.sumian.common.mvp.IPresenter
import com.sumian.sd.homepage.bean.CbtiChapterData

/**
 * Created by jzz
 *
 * on 2018-10-26.
 *
 * desc:
 *
 */
interface CBTIIntroductionContract {


    interface View : BaseShowLoadingView {

        fun getCBTIIntroductionListSuccess(cbtiChapterDataList: List<CbtiChapterData>)

        fun getCBTIIntroductionListFailed(error: String)

        fun getBannerInfo(formatExpiredTime: String, formatTotalProgress: String)
    }

    interface Presenter : IPresenter {


        fun getCBTIIntroductionList()
    }
}