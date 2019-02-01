package com.sumian.sddoctor.service.plan.contract

import com.sumian.common.mvp.BaseShowLoadingView
import com.sumian.common.mvp.IPresenter
import com.sumian.sddoctor.service.plan.bean.Plan

/**
 * Created by dq
 *
 * on 2018/8/30
 *
 * desc: 量表模块
 */
interface PlanContract {


    interface View : BaseShowLoadingView {

        fun onSendFollowPlansSuccess(success: String)

        fun onSendFollowPlansFailed(error: String)

        fun onGetFollowPlansSuccess(plans: List<Plan>)

        fun onGetFollowPlansFailed(error: String)

    }

    interface Presenter : IPresenter {

        fun getFollowPlans()

        fun sendFollowPlans(patientId: Int, followPlanId: Int)
    }
}