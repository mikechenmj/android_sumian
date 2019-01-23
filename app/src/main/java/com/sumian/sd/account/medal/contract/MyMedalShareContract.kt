package com.sumian.sd.account.medal.contract

import com.sumian.common.mvp.BaseShowLoadingView
import com.sumian.common.mvp.IPresenter
import com.sumian.sd.account.medal.bean.MyMedalShare

/**
 * Created by jzz
 *
 * on 2019/1/22
 *
 * desc:
 */
interface MyMedalShareContract {

    interface View : BaseShowLoadingView {
        fun onGetMyMedalSuccess(medalShare: MyMedalShare)
        fun onGetMyMedalFailed(error: String)
        fun onShareSuccess(shareUrlPath: String)
        fun onShareFailed(error: String)
    }

    interface Presenter : IPresenter {
        fun getMyMedal(id: Int)
        fun share(shareType: Int, medalShare: MyMedalShare)
    }
}