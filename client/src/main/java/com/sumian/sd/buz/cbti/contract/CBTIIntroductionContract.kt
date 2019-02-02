package com.sumian.sd.buz.cbti.contract

import com.sumian.common.base.BaseShowLoadingView
import com.sumian.sd.buz.homepage.bean.CbtiChapterData

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

        fun getCBTIServiceDetailSuccess(name: String, introduction: String, bannerUrl: String)

        fun getCBTIServiceDetailFailed(error: String)

        fun onCBTIServiceIsExpired(isExpired: Boolean)
    }

}