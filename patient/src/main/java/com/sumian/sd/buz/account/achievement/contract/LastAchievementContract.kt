package com.sumian.sd.buz.account.achievement.contract

import com.sumian.common.base.BaseShowLoadingView
import com.sumian.sd.buz.account.achievement.bean.LastAchievementData

/**
 * Created by jzz
 *
 * on 2019/1/23
 *
 * desc:
 */
interface LastAchievementContract {

    interface View : BaseShowLoadingView {
        fun onGetAchievementListForTypeSuccess(lastAchievementData: LastAchievementData)
        fun onGetAchievementListForTypeFailed(error: String) {}
        fun onPopAchievementSuccess(lastAchievementData: LastAchievementData) {}
        fun onPopAchievementFailed(error: String) {}
    }

}