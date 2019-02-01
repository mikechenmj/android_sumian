package com.sumian.sd.buz.account.achievement.contract

import com.sumian.common.mvp.BaseShowLoadingView
import com.sumian.common.mvp.IPresenter
import com.sumian.sd.buz.account.achievement.bean.Achievement
import com.sumian.sd.buz.account.achievement.bean.AchievementData
import com.sumian.sd.buz.account.achievement.bean.AchievementMeta

/**
 * Created by jzz
 *
 * on 2019/1/22
 *
 * desc:
 */
interface MyAchievementContract {

    interface View : BaseShowLoadingView {
        fun onGetMyAchievementListForTypeSuccess(achievementData: AchievementData)
        fun onGetMyAchievementListForTypeFailed(error: String)
        fun onGetMyAchievementListSuccess(achievementDataList: List<AchievementData>)
        fun onGetMyAchievementListTypeFailed(error: String)
        fun onGetMetaCallback(achievementMeta: AchievementMeta)
    }

    interface Presenter : IPresenter {
        fun getMyAchievement(type: Int = Achievement.CBTI_TYPE)
        fun getMyAchievementList()
    }
}