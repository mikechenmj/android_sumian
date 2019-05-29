package com.sumian.sd.buz.account.achievement.presenter

import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.account.achievement.bean.AchievementRecord
import com.sumian.sd.buz.account.achievement.contract.GetAchievementListContract
import com.sumian.sd.common.network.callback.BaseSdResponseCallback

/**
 * Created by jzz
 *
 * on 2019/2/21
 *
 * desc:获取个人信息勋章显示 icon 列表
 */
class GetAchievementListPresenter(private val view: GetAchievementListContract.View?) : GetAchievementListContract.ViewModel() {

    override fun getAchievementList() {
        view?.showLoading()
        val call = AppManager.getSdHttpService().getAchievementRecords()
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<List<AchievementRecord>>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                view?.onGetAchievementListFailed(error = errorResponse.message)
            }

            override fun onSuccess(response: List<AchievementRecord>?) {
                response?.let {
                    view?.onGetAchievementListSuccess(response)
                }
            }

            override fun onFinish() {
                super.onFinish()
                view?.dismissLoading()
            }
        })
    }

}