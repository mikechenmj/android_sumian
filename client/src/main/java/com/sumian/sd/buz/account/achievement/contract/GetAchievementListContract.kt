package com.sumian.sd.buz.account.achievement.contract

import com.sumian.common.base.BaseShowLoadingView
import com.sumian.common.base.BaseViewModel
import com.sumian.sd.buz.account.achievement.bean.AchievementRecord

/**
 * Created by jzz
 *
 * on 2019/2/21
 *
 * desc:
 */
interface GetAchievementListContract {

    interface View : BaseShowLoadingView {
        fun onGetAchievementListSuccess(achievementRecordList: List<AchievementRecord>)
        fun onGetAchievementListFailed(error: String)
    }

    abstract class ViewModel : BaseViewModel() {
        abstract fun getAchievementList()
    }
}