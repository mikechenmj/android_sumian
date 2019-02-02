package com.sumian.sddoctor.service.cbti.contract

import com.sumian.common.base.BaseShowLoadingView
import com.sumian.sddoctor.service.cbti.bean.CbtiChapterData

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
        fun getBannerInfo(formatTotalProgress: String)
        fun getCBTIServiceDetailSuccess(name: String, introduction: String, bannerUrl: String)
        fun getCBTIServiceDetailFailed(error: String)
        fun getConfigsSuccess()
        fun getConfigsFailed(error: String)
    }

}