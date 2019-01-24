package com.sumian.sd.account.achievement.contract

import com.sumian.common.mvp.BaseShowLoadingView
import com.sumian.common.mvp.IPresenter
import com.sumian.sd.account.achievement.bean.Achievement
import com.sumian.sd.account.achievement.bean.LastAchievementData

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

    interface Presenter : IPresenter {
        fun getLastAchievement(achievementCategoryType: Int = Achievement.CBTI_TYPE, achievementItemType: Int = -1)
        fun popAchievement(id: Int)
    }
}