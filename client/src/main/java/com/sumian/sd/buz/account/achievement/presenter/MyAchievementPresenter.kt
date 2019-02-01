package com.sumian.sd.buz.account.achievement.presenter

import com.sumian.common.base.BaseViewModel
import com.sumian.common.mvp.IPresenter.Companion.mCalls
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.account.achievement.bean.Achievement
import com.sumian.sd.buz.account.achievement.bean.AchievementData
import com.sumian.sd.buz.account.achievement.bean.AchievementResponse
import com.sumian.sd.buz.account.achievement.contract.MyAchievementContract
import com.sumian.sd.common.network.callback.BaseSdResponseCallback

/**
 * Created by jzz
 *
 * on 2019/1/22
 *
 * desc:
 */
class MyAchievementPresenter private constructor(private val view: MyAchievementContract.View?) : BaseViewModel(){


    companion object {
        @JvmStatic
        fun create(view: MyAchievementContract.View): MyAchievementPresenter = MyAchievementPresenter(view)
    }

     fun getMyAchievement(type: Int = Achievement.CBTI_TYPE) {
        view?.showLoading()
        val call = AppManager.getSdHttpService().getMyAchievementListForType(type)
         addCall(call)
        call.enqueue(object : BaseSdResponseCallback<AchievementData>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                view?.onGetMyAchievementListForTypeFailed(error = errorResponse.message)
            }

            override fun onSuccess(response: AchievementData?) {
                response?.let {
                    view?.onGetMyAchievementListForTypeSuccess(response)
                }
            }

            override fun onFinish() {
                super.onFinish()
                view?.dismissLoading()
            }
        })
    }

     fun getMyAchievementList() {
        view?.showLoading()
        val call = AppManager.getSdHttpService().getMyAchievementList()
         addCall(call)
        call.enqueue(object : BaseSdResponseCallback<AchievementResponse>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                view?.onGetMyAchievementListTypeFailed(error = errorResponse.message)
            }

            override fun onSuccess(response: AchievementResponse?) {
                response?.let {
                    val data = response.data
                    view?.onGetMyAchievementListSuccess(data)
                    view?.onGetMetaCallback(response.achievementMeta)
                }
            }

            override fun onFinish() {
                super.onFinish()
                view?.dismissLoading()
            }
        })
    }
}