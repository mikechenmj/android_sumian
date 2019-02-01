package com.sumian.sddoctor.service.plan.presenter

import com.sumian.common.base.BaseViewModel
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.service.plan.bean.Plan
import com.sumian.sddoctor.service.plan.contract.PlanContract

/**
 * Created by dq
 *
 * on 2018/8/30
 *
 * desc:
 */
class PlanPresenter private constructor(view: PlanContract.View) : BaseViewModel(){

    companion object {

        fun init(view: PlanContract.View): PlanPresenter {
            return PlanPresenter(view)
        }

    }

    private var mView: PlanContract.View? = null

    init {
        this.mView = view
    }

     fun getFollowPlans() {

        mView?.showLoading()

        val call = AppManager.getHttpService().getFollowupPlan()

        call.enqueue(object : BaseSdResponseCallback<List<Plan>>() {

            override fun onSuccess(response: List<Plan>?) {
                response?.let {
                    mView?.onGetFollowPlansSuccess(it)
                }

            }

            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.onGetFollowPlansFailed(error = errorResponse.message)
            }

            override fun onFinish() {
                super.onFinish()
                mView?.dismissLoading()
            }
        })

    }

     fun sendFollowPlans(patientId: Int, followPlanId: Int) {

        if (followPlanId == -1) {
            mView?.onSendFollowPlansFailed("未选择任何随访计划")
            return
        }

        mView?.showLoading()

        // val scaldIds = StringBuilder()

        // followPlanIds.forEachIndexed { _, id ->
        //     scaldIds.append("$id,")
        // }

        //scaldIds.delete(scaldIds.lastIndexOf(","), scaldIds.length)

        val map = mutableMapOf<String, Any>()

        map["follow_up_plan_ids"] = followPlanId
        map["user_ids"] = patientId

        val call = AppManager.getHttpService().sendFollowupPlan(map = map)

        call.enqueue(object : BaseSdResponseCallback<Void>() {

            override fun onSuccess(response: Void?) {
                mView?.onSendFollowPlansSuccess("发送成功")
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                mView?.onSendFollowPlansFailed(error = errorResponse.message)
            }

            override fun onFinish() {
                super.onFinish()
                mView?.dismissLoading()
            }
        })

    }
}