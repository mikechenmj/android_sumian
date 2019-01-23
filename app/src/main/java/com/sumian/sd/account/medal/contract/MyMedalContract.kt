package com.sumian.sd.account.medal.contract

import com.sumian.common.mvp.BaseShowLoadingView
import com.sumian.common.mvp.IPresenter
import com.sumian.sd.account.medal.bean.Data
import com.sumian.sd.account.medal.bean.Meta

/**
 * Created by jzz
 *
 * on 2019/1/22
 *
 * desc:
 */
interface MyMedalContract {

    interface View : BaseShowLoadingView {
        fun onGetMyMedalListSuccess(myMedalList: List<Data>)
        fun onGetMyMedalListFailed(error: String)
        fun onGetMetaCallback(meta: Meta)
    }

    interface Presenter : IPresenter {
        fun getMyMetal()
    }
}