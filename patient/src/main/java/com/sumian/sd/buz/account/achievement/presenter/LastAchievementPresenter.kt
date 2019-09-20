package com.sumian.sd.buz.account.achievement.presenter

import com.sumian.common.base.BaseViewModel
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.account.achievement.bean.Achievement
import com.sumian.sd.buz.account.achievement.bean.LastAchievementData
import com.sumian.sd.buz.account.achievement.contract.LastAchievementContract
import com.sumian.sd.common.network.callback.BaseSdResponseCallback

/**
 * Created by jzz
 *
 * on 2019/1/23
 *
 * desc:
 */
class LastAchievementPresenter constructor(var view: LastAchievementContract.View?) : BaseViewModel() {

    fun getLastAchievement(achievementCategoryType: Int = Achievement.CBTI_TYPE, achievementItemType: Int = -1) {
        //view?.showLoading()
        val map = mutableMapOf<String, Any>()
        map["achievement_category_type"] = achievementCategoryType
        if (achievementItemType != -1) {
            map["achievement_type"]
        }
        val call = AppManager.getSdHttpService().getLastAchievement(map)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<LastAchievementData>() {
            override fun onSuccess(response: LastAchievementData?) {
                response?.let {
                    if (response.isPop()) return
                    view?.onGetAchievementListForTypeSuccess(response)
                    popAchievement(response.id)
                }
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                view?.onGetAchievementListForTypeFailed(error = errorResponse.message)
            }

            override fun onFinish() {
                super.onFinish()
                //view?.dismissLoading()
            }
        })

    }

    fun popAchievement(id: Int) {
        //view?.showLoading()
        val call = AppManager.getSdHttpService().popAchievement(id)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<LastAchievementData>() {
            override fun onSuccess(response: LastAchievementData?) {
                response?.let {
                    view?.onPopAchievementSuccess(response)
                }
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                view?.onPopAchievementFailed(error = errorResponse.message)
            }

            override fun onFinish() {
                super.onFinish()
                //view?.dismissLoading()
            }
        })
    }
}